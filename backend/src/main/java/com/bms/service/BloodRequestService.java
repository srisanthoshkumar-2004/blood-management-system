package com.bms.service;

import com.bms.dto.BloodRequestDTO;
import com.bms.entity.BloodRequest;
import com.bms.entity.DonorResponse;
import com.bms.entity.PatientHistory;
import com.bms.entity.User;
import com.bms.repository.BloodRequestRepository;
import com.bms.repository.DonorResponseRepository;
import com.bms.repository.PatientHistoryRepository;
import com.bms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BloodRequestService {

    private final BloodRequestRepository bloodRequestRepository;
    private final DonorResponseRepository donorResponseRepository;
    private final UserRepository userRepository;
    private final PatientHistoryRepository patientHistoryRepository;
    private final com.bms.repository.DonationHistoryRepository donationHistoryRepository;
    private final TwilioService twilioService;
    private final EmergencyMatchingService emergencyMatchingService;

    @Transactional
    public BloodRequest createRequest(BloodRequestDTO dto, String username) {
        // SECURITY: Create a fresh entity and copy only allowed fields from the validated DTO.
        // This makes the application 100% immune to mass-assignment attacks.
        BloodRequest request = BloodRequest.builder()
                .patientName(dto.getPatientName())
                .hospitalName(dto.getHospitalName())
                .contactNumber(dto.getContactNumber())
                .bloodGroup(dto.getBloodGroup())
                .location(dto.getLocation())
                .emergencyLevel(dto.getEmergencyLevel())
                .requestDate(LocalDateTime.now())
                .status(BloodRequest.RequestStatus.PENDING)
                .build();
        
        BloodRequest savedRequest = bloodRequestRepository.save(request);

        if (username != null) {
            User user = userRepository.findByUsername(username).orElseThrow();
            request.setRequestedBy(user);
            
            // Become a Patient, remove from donor pool
            user.setIsDonor(false);
            user.setIsPatient(true);
            user.setAvailable(false);
            user.setPatientSince(java.time.LocalDate.now());
            userRepository.save(user);

            // Add PatientHistory implicitly
            PatientHistory history = PatientHistory.builder()
                .patient(user)
                .bloodRequest(savedRequest)
                .requestDetails("Blood Request for " + request.getPatientName() + " (" + request.getBloodGroup() + ") Emergency: " + request.getEmergencyLevel())
                .hospitalName(request.getHospitalName())
                .status(BloodRequest.RequestStatus.PENDING.name())
                .requestDate(LocalDateTime.now())
                .build();
            patientHistoryRepository.save(history);
        }
        
        // Trigger Async matching
        emergencyMatchingService.startEmergencyMatching(savedRequest);
        
        return savedRequest;
    }

    @Transactional
    public String handleDonorResponse(UUID requestId, UUID donorId, DonorResponse.ResponseStatus responseStatus) {
        // Use Pessimistic Lock to prevent race conditions
        BloodRequest request = bloodRequestRepository.findByIdWithLock(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        
        User donor = userRepository.findById(donorId).orElseThrow(() -> new RuntimeException("Donor not found"));

        if (request.getStatus() != BloodRequest.RequestStatus.PROCESSING && 
            request.getStatus() != BloodRequest.RequestStatus.PENDING) {
            return "Request is no longer active. It has already been " + request.getStatus();
        }

        List<DonorResponse> responses = donorResponseRepository.findByRequest(request);
        DonorResponse donorResponse = responses.stream()
                .filter(r -> r.getDonor().getId().equals(donorId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Donor not found in recipient list"));

        if (donorResponse.getResponse() != DonorResponse.ResponseStatus.PENDING) {
            return "Response already recorded";
        }

        donorResponse.setResponse(responseStatus);
        donorResponse.setResponseTime(LocalDateTime.now());
        donorResponseRepository.saveAndFlush(donorResponse);
        
        if (responseStatus == DonorResponse.ResponseStatus.YES) {
            log.info("Request {} FULFILLED safely by {} - {}", requestId, donor.getRole(), donor.getUsername());
            
            // 1. Update Request Status
            request.setStatus(BloodRequest.RequestStatus.FULFILLED);
            bloodRequestRepository.saveAndFlush(request);
            log.info("Persisted BloodRequest FULFILLED status for ID: {}", requestId);
            
            // 2. Update Donor Availability
            donor.setAvailable(false);
            userRepository.saveAndFlush(donor);
            
            // 3. Update Donor History
            com.bms.entity.DonationHistory donationHistory = com.bms.entity.DonationHistory.builder()
                    .donor(donor)
                    .donationDate(java.time.LocalDate.now())
                    .bloodGroup(request.getBloodGroup())
                    .hospitalName(request.getHospitalName())
                    .unitsDonated(1)
                    .build();
            donationHistoryRepository.saveAndFlush(donationHistory);

            // 4. Update Patient History
            patientHistoryRepository.findByBloodRequestId(request.getId()).ifPresentOrElse(ph -> {
                ph.setStatus(BloodRequest.RequestStatus.FULFILLED.name());
                ph.setFulfilledDonor(donor);
                patientHistoryRepository.saveAndFlush(ph);
                log.info("Updated PatientHistory record to FULFILLED for Patient ID: {}", ph.getPatient().getId());
            }, () -> {
                log.warn("PatientHistory not found for Request ID: {}", request.getId());
            });
            
            // 5. Notify Patient with tailored message
            String patientMsg;
            String voiceMsg;
            
            if (donor.getRole() == com.bms.entity.Role.BLOOD_BANK_MANAGER) {
                patientMsg = String.format("URGENT UPDATE: A Blood Bank Manager (%s) has successfully accepted your request. Please reach them at %s immediately.", 
                        donor.getUsername(), donor.getPhone());
                voiceMsg = "Good news. A blood bank manager has been found for your request.";
            } else {
                patientMsg = String.format("URGENT UPDATE: A donor has accepted your request. %s (%s) is on the way. Please contact them.", 
                        donor.getUsername(), donor.getPhone());
                voiceMsg = "Good news. A donor has been found for your request.";
            }
            
            twilioService.sendSms(request.getContactNumber(), patientMsg);
            twilioService.makeCall(request.getContactNumber(), voiceMsg);
            
            return "Thank you! You have successfully accepted this request. Contact details for the patient have been sent to you via SMS.";
        } else {
            return "Response recorded. Thank you for your time.";
        }
    }
}

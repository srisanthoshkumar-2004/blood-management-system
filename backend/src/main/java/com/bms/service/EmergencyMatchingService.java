package com.bms.service;

import com.bms.entity.BloodRequest;
import com.bms.entity.DonorResponse;
import com.bms.entity.Role;
import com.bms.entity.User;
import com.bms.repository.BloodRequestRepository;
import com.bms.repository.DonorResponseRepository;
import com.bms.repository.PatientHistoryRepository;
import com.bms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmergencyMatchingService {

    private final UserRepository userRepository;
    private final BloodRequestRepository bloodRequestRepository;
    private final DonorResponseRepository responseRepository;
    private final PatientHistoryRepository patientHistoryRepository;
    private final TwilioService twilioService;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Value("${app.backend-url}")
    private String backendUrl;

    private static final int WAIT_TIME_MS = 300000; // 5 minutes

    @Async
    public void startEmergencyMatching(BloodRequest request) {
        // Fetch the freshest version from DB to avoid stale objects
        BloodRequest currentRequest = bloodRequestRepository.findById(request.getId()).orElse(request);
        log.info("Starting emergency matching for request ID: {}", currentRequest.getId());

        currentRequest.setStatus(BloodRequest.RequestStatus.PROCESSING);
        bloodRequestRepository.saveAndFlush(currentRequest);
        
        // STEP 1: Notify Blood Bank Managers via SMS
        List<User> bankManagers = userRepository.findByRoleAndLocation(Role.BLOOD_BANK_MANAGER, request.getLocation());
        if (!bankManagers.isEmpty()) {
            for (User bankManager : bankManagers) {
                if (isRequestFulfilled(request.getId())) return;
                String msg = String.format("URGENT: %s Blood needed at %s. Reply YES to this link: %s/respond/%s/donor/%s within 5 minutes.",
                        request.getBloodGroup(), request.getHospitalName(), frontendUrl, request.getId(), bankManager.getId());
                saveResponse(bankManager, request, 0); // Batch 0 for Banks
                twilioService.sendSms(bankManager.getPhone(), msg);
            }

            if (waitForResponseOrFulfillment(request, 0, WAIT_TIME_MS)) {
                log.info("Request {} FULFILLED safely by Blood Bank Manager.", request.getId());
                return; // STOP!
            }

            // STEP 2: Call Blood Bank Managers
            for (User bankManager : bankManagers) {
                if (isRequestFulfilled(request.getId())) return;
                
                // SKIPPING REDUNDANT CALLS FOR THOSE WHO ALREADY REJECTED
                if (hasAlreadyRejected(request.getId(), bankManager.getId())) {
                    log.info("Skipping call for manager {} who already rejected via SMS.", bankManager.getUsername());
                    continue;
                }

                saveResponse(bankManager, request, 0); // Ensure record exists
                String callMsg = "Hello. This is an emergency alert from Blood Management System. A patient at " + request.getHospitalName() + " requires " + request.getBloodGroup() + " blood urgently. Press 1 if you are available to donate. press 2 to reject";
                twilioService.makeEmergencyCall(bankManager.getPhone(), callMsg, request.getId(), bankManager.getId(), backendUrl);
            }
            if (waitForResponseOrFulfillment(request, 0, WAIT_TIME_MS)) {
                log.info("Request {} FULFILLED safely by Blood Bank Manager via Call.", request.getId());
                return; // STOP!
            }
        }

        // STEP 3: Individual Donors SMS (ALL AT ONCE - Batch 1)
        List<User> eligibleDonors = userRepository.findEligibleDonors(request.getBloodGroup(), request.getLocation());
        log.info("Found {} individual donors for request ID: {}", eligibleDonors.size(), request.getId());

        if (eligibleDonors.isEmpty()) {
            if (bankManagers.isEmpty()) {
                failRequest(request, "No eligible donors or blood banks found in the location.");
            } else {
                failRequest(request, "Blood banks did not respond and no eligible individual donors found.");
            }
            return;
        }

        log.info("Sending SMS to ALL {} donors at once.", eligibleDonors.size());
        for (User donor : eligibleDonors) {
            if (isRequestFulfilled(request.getId())) return;
            saveResponse(donor, request, 1);
            String msg = String.format("URGENT: %s Blood needed at %s. Please reply YES to this link: %s/respond/%s/donor/%s within 5 minutes.",
                    request.getBloodGroup(), request.getHospitalName(), frontendUrl, request.getId(), donor.getId());
            twilioService.sendSms(donor.getPhone(), msg);
        }

        if (waitForResponseOrFulfillment(request, 1, WAIT_TIME_MS)) {
            log.info("Request {} FULFILLED safely by Individual Donor via SMS.", request.getId());
            return; // STOP!
        } else {
            markPendingAsTimeout(request.getId(), 1);
        }

        // STEP 4: Call Escalation for Individual Donors
        log.info("Entering Call Escalation phase for request {}", request.getId());
        for (User donor : eligibleDonors) {
            if (isRequestFulfilled(request.getId())) return;
            
            // SKIPPING REDUNDANT CALLS FOR THOSE WHO ALREADY REJECTED
            if (hasAlreadyRejected(request.getId(), donor.getId())) {
                log.info("Skipping call for donor {} who already rejected via SMS.", donor.getUsername());
                continue;
            }

            saveResponse(donor, request, -1); // Stage 4 fallback batch
            String callMsg = "Hello. This is an emergency alert from Blood Management System. A patient at " + request.getHospitalName() + " requires " + request.getBloodGroup() + " blood urgently. Press 1 if you are available to donate. press 2 to reject";
            twilioService.makeEmergencyCall(donor.getPhone(), callMsg, request.getId(), donor.getId(), backendUrl);
        }

        if (waitForResponseOrFulfillment(request, -1, WAIT_TIME_MS)) {
            log.info("Request {} FULFILLED safely by Individual Donor via Call.", request.getId());
            return; // STOP!
        }

        // FAILED
        failRequest(request, "Wait time expired. No donors or banks accepted the request.");
    }

    private DonorResponse saveResponse(User user, BloodRequest request, int batchNum) {
        return responseRepository.findByRequest(request).stream()
                .filter(r -> r.getDonor().getId().equals(user.getId()))
                .findFirst()
                .orElseGet(() -> {
                    DonorResponse donorResponse = DonorResponse.builder()
                            .donor(user)
                            .request(request)
                            .response(DonorResponse.ResponseStatus.PENDING)
                            .responseTime(LocalDateTime.now())
                            .batchNumber(batchNum)
                            .build();
                    return responseRepository.save(donorResponse);
                });
    }

    private boolean isRequestFulfilled(java.util.UUID requestId) {
        return bloodRequestRepository.findById(requestId)
                .map(r -> r.getStatus() == BloodRequest.RequestStatus.FULFILLED)
                .orElse(false);
    }

    /**
     * Polls the database every 5 seconds to instantly check if the request was fulfilled by a transactional controller connection.
     * Prevents blind Thread.sleep and breaks out exactly when fulfilled to prevent duplicate acceptances.
     */
    private boolean waitForResponseOrFulfillment(BloodRequest request, Integer batchNumber, int timeoutMs) {
        int slept = 0;
        int interval = 2000; // Increased speed from 5s to 2s
        
        while (slept < timeoutMs) {
            try { Thread.sleep(interval); } catch (Exception ignored) {}
            slept += interval;
            
            BloodRequest current = bloodRequestRepository.findById(request.getId()).orElse(request);
            if (current.getStatus() == BloodRequest.RequestStatus.FULFILLED) {
                return true;
            }

            // Optimization: Early exit if everyone in this batch has already replied
            List<DonorResponse> batchResponses;
            if (batchNumber != null) {
                batchResponses = responseRepository.findByRequestIdAndBatchNumber(request.getId(), batchNumber);
            } else {
                batchResponses = responseRepository.findByRequest(current);
            }

            boolean anyPending = batchResponses.stream().anyMatch(r -> r.getResponse() == DonorResponse.ResponseStatus.PENDING);
            if (!anyPending && !batchResponses.isEmpty()) {
                log.info("Early exit for batch {}: No pending responses left.", batchNumber);
                return false; 
            }
        }
        return false;
    }

    private void markPendingAsTimeout(java.util.UUID requestId, Integer batchNumber) {
        List<DonorResponse> batchResponses = responseRepository.findByRequestIdAndBatchNumber(requestId, batchNumber);
        for (DonorResponse r : batchResponses) {
            if (r.getResponse() == DonorResponse.ResponseStatus.PENDING) {
                r.setResponse(DonorResponse.ResponseStatus.TIMEOUT);
                responseRepository.save(r);
            }
        }
    }

    private void failRequest(BloodRequest request, String reason) {
        log.info("Request ID {} failed: {}", request.getId(), reason);
        // Ensure atomic update, just in case a concurrent update happened.
        BloodRequest current = bloodRequestRepository.findById(request.getId()).orElse(request);
        if (current.getStatus() != BloodRequest.RequestStatus.FULFILLED) {
            // 1. Update BloodRequest Status
            current.setStatus(BloodRequest.RequestStatus.FAILED);
            bloodRequestRepository.saveAndFlush(current);
            
            // 2. Update PatientHistory Record
            patientHistoryRepository.findByBloodRequestId(current.getId()).ifPresent(ph -> {
                ph.setStatus(BloodRequest.RequestStatus.FAILED.name());
                patientHistoryRepository.saveAndFlush(ph);
                log.info("PatientHistory marked as FAILED for Request ID: {}", current.getId());
            });
            
            // 3. Notify Patient
            twilioService.sendSms(current.getContactNumber(), 
                "Blood Management System: We could not find a donor at this exact moment. Please try searching again after 15 minutes.");
        }
    }

    private boolean hasAlreadyRejected(java.util.UUID requestId, java.util.UUID userId) {
        return responseRepository.findByRequestIdAndDonorId(requestId, userId)
                .map(r -> r.getResponse() == DonorResponse.ResponseStatus.NO)
                .orElse(false);
    }
}

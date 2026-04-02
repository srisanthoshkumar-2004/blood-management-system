package com.bms.controller;

import com.bms.dto.DonationHistoryDTO;
import com.bms.entity.DonationHistory;
import com.bms.entity.User;
import com.bms.repository.DonationHistoryRepository;
import com.bms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/api/donor")
@RequiredArgsConstructor
public class DonationHistoryController {

    private final DonationHistoryRepository historyRepository;
    private final UserRepository userRepository;

    @GetMapping("/{id}/history")
    @PreAuthorize("@ownershipValidator.isSelf(#id)")
    public ResponseEntity<List<DonationHistory>> getHistory(@PathVariable java.util.UUID id) {
        return ResponseEntity.ok(historyRepository.findByDonorIdOrderByDonationDateDesc(id));
    }

    @PostMapping("/{id}/history")
    @PreAuthorize("@ownershipValidator.isSelf(#id)")
    public ResponseEntity<?> addHistory(@PathVariable java.util.UUID id, @Valid @RequestBody DonationHistoryDTO historyDTO) {
        User user = userRepository.findById(id).orElseThrow();
        
        List<DonationHistory> pastDonations = historyRepository.findByDonorIdOrderByDonationDateDesc(id);
        if (!pastDonations.isEmpty()) {
            LocalDate lastDonation = pastDonations.get(0).getDonationDate();
            long daysSince = ChronoUnit.DAYS.between(lastDonation, historyDTO.getDonationDate());
            if (daysSince < 90) {
                return ResponseEntity.badRequest().body("Minimum 90-day gap between donations is required. Days since last donation: " + daysSince);
            }
        }
        
        // SECURITY: Create fresh entity and copy only allowed fields from validated DTO
        DonationHistory history = DonationHistory.builder()
                .donor(user)
                .donationDate(historyDTO.getDonationDate())
                .bloodGroup(historyDTO.getBloodGroup())
                .hospitalName(historyDTO.getHospitalName())
                .unitsDonated(historyDTO.getUnitsDonated())
                .build();
        
        DonationHistory savedHistory = historyRepository.save(history);
        
        user.setLastDonationDate(history.getDonationDate());
        userRepository.save(user);
        
        return ResponseEntity.ok(savedHistory);
    }
}

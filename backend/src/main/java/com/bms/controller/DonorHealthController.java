package com.bms.controller;

import com.bms.dto.DonorHealthDTO;
import com.bms.entity.DonorHealth;
import com.bms.entity.User;
import com.bms.repository.DonorHealthRepository;
import com.bms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class DonorHealthController {

    private final DonorHealthRepository healthRepository;
    private final UserRepository userRepository;

    @PostMapping("/update")
    @PreAuthorize("@ownershipValidator.isSelf(#userId)")
    public ResponseEntity<DonorHealth> updateHealth(@Valid @RequestBody DonorHealthDTO healthRequest, @RequestParam java.util.UUID userId) {
        User user = userRepository.findById(userId).orElseThrow();
        
        Optional<DonorHealth> existing = healthRepository.findByUser(user);
        DonorHealth health = existing.orElseGet(() -> DonorHealth.builder().user(user).build());
        
        // SECURITY: Manually map validated fields from DTO to Entity
        health.setWeight(healthRequest.getWeight());
        health.setHemoglobinLevel(healthRequest.getHemoglobinLevel());
        health.setBloodPressure(healthRequest.getBloodPressure());
        health.setLastCheckupDate(healthRequest.getLastCheckupDate());
        health.setIsEligible(healthRequest.getIsEligible());
        health.setMedicalConditions(healthRequest.getMedicalConditions());
        
        return ResponseEntity.ok(healthRepository.save(health));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("@ownershipValidator.isSelf(#userId)")
    public ResponseEntity<DonorHealth> getHealth(@PathVariable java.util.UUID userId) {
        return healthRepository.findByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

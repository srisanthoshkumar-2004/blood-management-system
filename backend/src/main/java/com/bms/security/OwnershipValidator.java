package com.bms.security;

import com.bms.entity.BloodRequest;
import com.bms.entity.DonorResponse;
import com.bms.entity.User;
import com.bms.repository.BloodRequestRepository;
import com.bms.repository.DonorResponseRepository;
import com.bms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Enterprise Ownership Validator
 * Ensures that users can only access or modify resources they own.
 * Prevents IDOR (Insecure Direct Object Reference) vulnerabilities.
 */
@Component("ownershipValidator")
@RequiredArgsConstructor
public class OwnershipValidator {

    private final UserRepository userRepository;
    private final BloodRequestRepository bloodRequestRepository;
    private final DonorResponseRepository donorResponseRepository;

    /**
     * Checks if the authenticated user is the same as the target user ID.
     */
    public boolean isSelf(UUID userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;
        
        String currentUsername = auth.getName();
        return userRepository.findById(userId)
                .map((User user) -> user.getUsername().equals(currentUsername))
                .orElse(false);
    }

    /**
     * Checks if the authenticated user is the patient who created the blood request.
     */
    public boolean isRequestOwner(UUID requestId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;
        
        String currentUsername = auth.getName();
        return bloodRequestRepository.findById(requestId)
                .map((BloodRequest request) -> {
                    if (request.getRequestedBy() == null) return false; // Deny access — no owner to verify
                    return request.getRequestedBy().getUsername().equals(currentUsername);
                })
                .orElse(false);
    }

    /**
     * Checks if the authenticated user is the donor who provided the response.
     */
    public boolean isResponseOwner(UUID responseId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;
        
        String currentUsername = auth.getName();
        return donorResponseRepository.findById(responseId)
                .map((DonorResponse response) -> response.getDonor().getUsername().equals(currentUsername))
                .orElse(false);
    }
    
    /**
     * Checks if the authenticated user is the owner of a user-linked resource (e.g., DonorHealth).
     */
    public boolean isResourceOwner(UUID userId) {
        return isSelf(userId);
    }
}

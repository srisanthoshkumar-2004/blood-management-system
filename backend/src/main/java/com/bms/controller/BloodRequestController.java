package com.bms.controller;

import com.bms.dto.BloodRequestDTO;
import com.bms.entity.BloodRequest;
import com.bms.entity.DonorResponse;
import com.bms.entity.User;
import com.bms.repository.BloodRequestRepository;
import com.bms.repository.DonorResponseRepository;
import com.bms.repository.UserRepository;
import com.bms.service.BloodRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/request")
@RequiredArgsConstructor
public class BloodRequestController {

    private final BloodRequestService bloodRequestService;
    private final BloodRequestRepository bloodRequestRepository;
    private final UserRepository userRepository;
    private final DonorResponseRepository donorResponseRepository;

    @GetMapping("/managed")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('BLOOD_BANK_MANAGER')")
    public ResponseEntity<List<BloodRequest>> getManagedRequests(Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        User manager = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Manager not found"));
        return ResponseEntity.ok(bloodRequestRepository.findByLocationOrderByRequestDateDesc(manager.getLocation()));
    }

    @PostMapping("/blood")
    public ResponseEntity<BloodRequest> createRequest(@Valid @RequestBody BloodRequestDTO requestDTO, Principal principal) {
        String username = (principal != null) ? principal.getName() : null;
        return ResponseEntity.ok(bloodRequestService.createRequest(requestDTO, username));
    }

    @GetMapping("/history/{userId}")
    @org.springframework.security.access.prepost.PreAuthorize("@ownershipValidator.isSelf(#userId)")
    public ResponseEntity<List<BloodRequest>> getRequestHistory(@PathVariable java.util.UUID userId) {
        return ResponseEntity.ok(bloodRequestRepository.findByRequestedByIdOrderByRequestDateDesc(userId));
    }
    
    @GetMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("@ownershipValidator.isRequestOwner(#id)")
    public ResponseEntity<BloodRequest> getRequest(@PathVariable java.util.UUID id) {
        return bloodRequestRepository.findById(id)
                .map(req -> ResponseEntity.ok()
                        .header("Cache-Control", "no-cache, no-store, must-revalidate")
                        .body(req))
                .orElse(ResponseEntity.notFound().build());
    }

    // Accessible via unguessable capability URL (UUIDs acting as secure tokens)
    @PostMapping("/respond")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<?> respondToRequest(
            @RequestParam java.util.UUID requestId,
            @RequestParam java.util.UUID donorId,
            @RequestParam DonorResponse.ResponseStatus responseStatus) {
        
        String result = bloodRequestService.handleDonorResponse(requestId, donorId, responseStatus);
        
        if (result.contains("Thank you") || result.contains("Response recorded")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    // Accessible via unguessable capability URL (UUIDs acting as secure tokens)
    @GetMapping("/check-status")
    public ResponseEntity<?> checkStatus(@RequestParam java.util.UUID requestId, @RequestParam java.util.UUID donorId) {
        BloodRequest request = bloodRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        
        List<DonorResponse> responses = donorResponseRepository.findByRequest(request);
        DonorResponse donorResponse = responses.stream()
                .filter(r -> r.getDonor().getId().equals(donorId))
                .findFirst()
                .orElse(null);

        Map<String, Object> statusMap = new HashMap<>();
        statusMap.put("requestStatus", request.getStatus());
        statusMap.put("donorResponse", donorResponse != null ? donorResponse.getResponse() : null);
        
        return ResponseEntity.ok()
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .body(statusMap);
    }
}

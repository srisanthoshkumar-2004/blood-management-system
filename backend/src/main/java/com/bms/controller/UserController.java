package com.bms.controller;

import com.bms.dto.UserDTO;
import com.bms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/{id}/availability")
    @PreAuthorize("@ownershipValidator.isSelf(#id)")
    public ResponseEntity<UserDTO> updateAvailability(@PathVariable java.util.UUID id, @RequestParam Boolean available) {
        return ResponseEntity.ok(userService.updateUserAvailability(id, available));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("@ownershipValidator.isSelf(#id)")
    public ResponseEntity<UserDTO> getUser(@PathVariable java.util.UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/donors/search")
    @PreAuthorize("hasAnyRole('BLOOD_BANK_MANAGER', 'ADMIN')")
    public ResponseEntity<List<UserDTO>> searchDonors(
            @RequestParam String bloodGroup,
            @RequestParam String location) {
        return ResponseEntity.ok(userService.searchDonors(bloodGroup, location));
    }
}

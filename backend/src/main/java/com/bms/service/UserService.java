package com.bms.service;

import com.bms.dto.UserDTO;
import com.bms.entity.User;
import com.bms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDTO updateUserAvailability(java.util.UUID id, Boolean available) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setAvailable(available);
        return mapToDTO(userRepository.save(user));
    }

    public UserDTO getUserById(java.util.UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return mapToDTO(user);
    }
    
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        return mapToDTO(user);
    }

    public List<UserDTO> searchDonors(String bloodGroup, String location) {
        return userRepository.findEligibleDonors(bloodGroup, location).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .bloodGroup(user.getBloodGroup())
                .location(user.getLocation())
                .phone(user.getPhone())
                .age(user.getAge())
                .available(user.getAvailable())
                .lastDonationDate(user.getLastDonationDate())
                .isDonor(user.getIsDonor())
                .isPatient(user.getIsPatient())
                .patientSince(user.getPatientSince())
                .build();
    }
}

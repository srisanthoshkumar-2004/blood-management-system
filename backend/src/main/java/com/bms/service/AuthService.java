package com.bms.service;

import com.bms.dto.AuthRequest;
import com.bms.dto.AuthResponse;
import com.bms.dto.RegisterRequest;
import com.bms.entity.Role;
import com.bms.entity.User;
import com.bms.repository.UserRepository;
import com.bms.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent() ||
            userRepository.findByEmail(request.getEmail()).isPresent()) {
            return AuthResponse.builder().message("Username or Email already exists").build();
        }

        // Determine role and initial flags
        // SECURITY: Block ADMIN self-registration — ADMIN accounts must be created internally
        Role assignedRole = (request.getRole() != null) ? request.getRole() : Role.DONOR;
        if (assignedRole == Role.ADMIN) {
            return AuthResponse.builder().message("Invalid role selection. Admin accounts cannot be self-registered.").build();
        }
        boolean isDonorInitial = (assignedRole == Role.DONOR);
        boolean isPatientInitial = (assignedRole == Role.PATIENT);
        
        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(assignedRole)
                .bloodGroup(request.getBloodGroup())
                .location(request.getLocation())
                .phone(request.getPhone())
                .age(request.getAge())
                .available(isDonorInitial) // Only donors are available by default
                .isDonor(isDonorInitial)
                .isPatient(isPatientInitial)
                .favoritePlaceHash(passwordEncoder.encode(request.getFavoritePlace() == null ? "N/A" : request.getFavoritePlace().trim().toLowerCase()))
                .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshTokenEntity = refreshTokenService.createRefreshToken(user.getId());
        
        return AuthResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshTokenEntity.getToken())
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .message("Registration successful")
                .build();
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshTokenEntity = refreshTokenService.createRefreshToken(user.getId());
        
        return AuthResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshTokenEntity.getToken())
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .message("Login successful")
                .build();
    }

    public boolean resetPassword(com.bms.dto.ResetPasswordRequest request) {
        var userOpt = userRepository.findByUsername(request.getIdentifier());
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(request.getIdentifier());
        }
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Compare the favorite place answer (normalize it)
            String answerToHash = request.getFavoritePlace().trim().toLowerCase();
            if (passwordEncoder.matches(answerToHash, user.getFavoritePlaceHash())) {
                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    public java.util.Optional<com.bms.entity.RefreshToken> getRefreshToken(String token) {
        return refreshTokenService.findByToken(token);
    }

    public AuthResponse refreshAccessToken(com.bms.entity.RefreshToken refreshToken) {
        refreshTokenService.verifyExpiration(refreshToken);
        User user = refreshToken.getUser();
        String jwtToken = jwtService.generateToken(user);
        
        return AuthResponse.builder()
                .token(jwtToken)
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .message("Token refreshed successfully")
                .build();
    }
}

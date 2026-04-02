package com.bms.dto;

import com.bms.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private java.util.UUID id;
    private String username;
    private Role role;
    private String message;
    private String refreshToken;
}

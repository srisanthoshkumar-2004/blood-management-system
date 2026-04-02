package com.bms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 3, message = "Username must be at least 3 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private String bloodGroup; // Can be null for BLOOD_BANK_MANAGER

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "Phone number is required")
    @Size(min = 10, message = "Phone number must be at least 10 digits")
    private String phone;

    private Integer age;

    private com.bms.entity.Role role;

    @NotBlank(message = "Favorite place security question answer is required")
    private String favoritePlace;
}

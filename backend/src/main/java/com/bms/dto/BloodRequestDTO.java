package com.bms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Enterprise Data Transfer Object for Blood Request.
 * Separates the database entity from the public API to prevent mass-assignment
 * and enforce strict validation on all incoming patient requests.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BloodRequestDTO {

    @NotBlank(message = "Patient name is required")
    @Size(min = 2, max = 100, message = "Patient name must be between 2 and 100 characters")
    private String patientName;

    @NotBlank(message = "Hospital name is required")
    @Size(min = 2, max = 200, message = "Hospital name must be between 2 and 200 characters")
    private String hospitalName;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid contact number format. Use E.164 format (e.g., +919876543210)")
    private String contactNumber;

    @NotBlank(message = "Blood group is required")
    @Pattern(regexp = "^(A|B|AB|O)[+-]$", message = "Invalid blood group. Expected format: A+, B-, AB+, O+, etc.")
    private String bloodGroup;

    @NotBlank(message = "Location is required")
    @Size(min = 2, max = 100, message = "Location must be between 2 and 100 characters")
    private String location;

    @NotBlank(message = "Emergency level is required")
    @Pattern(regexp = "^(CRITICAL|HIGH|MODERATE)$", message = "Invalid emergency level. Must be CRITICAL, HIGH, or MODERATE")
    private String emergencyLevel;
}

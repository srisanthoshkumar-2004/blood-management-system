package com.bms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DonorHealthDTO {

    @Min(value = 45, message = "Weight must be at least 45kg")
    private Double weight;

    @DecimalMin(value = "7.0", message = "Hemoglobin level must be at least 7.0 g/dL")
    @DecimalMax(value = "20.0", message = "Hemoglobin level must be realistic")
    private Double hemoglobinLevel;

    @NotBlank(message = "Blood pressure is required")
    private String bloodPressure;

    private LocalDate lastCheckupDate;

    private Boolean isEligible;

    private String medicalConditions;
}

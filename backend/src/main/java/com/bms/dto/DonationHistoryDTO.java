package com.bms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DonationHistoryDTO {

    @NotNull(message = "Donation date is required")
    private LocalDate donationDate;

    @NotBlank(message = "Blood group is required")
    @Pattern(regexp = "^(A|B|AB|O)[+-]$", message = "Invalid blood group")
    private String bloodGroup;

    @NotBlank(message = "Hospital name is required")
    private String hospitalName;

    @Min(value = 1, message = "At least 1 unit must be donated")
    private int unitsDonated;
}

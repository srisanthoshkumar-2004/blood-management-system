package com.bms.dto;

import com.bms.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private java.util.UUID id;
    private String username;
    private String email;
    private Role role;
    private String bloodGroup;
    private String location;
    private String phone;
    private Integer age;
    private Boolean available;
    private LocalDate lastDonationDate;
    private Boolean isDonor;
    private Boolean isPatient;
    private LocalDate patientSince;
}

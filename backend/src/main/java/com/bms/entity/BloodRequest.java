package com.bms.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

@Entity
@Data
@Getter
@Setter
@Builder
@NoArgsConstructor 
@AllArgsConstructor
public class BloodRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private java.util.UUID id;

    @Version
    private Long version;

    @NotBlank(message = "Patient name is required")
    private String patientName;

    @NotBlank(message = "Hospital name is required")
    private String hospitalName;

    @NotBlank(message = "Contact number is required")
    private String contactNumber;

    @NotBlank(message = "Blood group is required")
    @Pattern(regexp = "^(A|B|AB|O)[+-]$")
    private String bloodGroup;

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "Emergency level is required")
    private String emergencyLevel;

    private LocalDateTime requestDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by_user_id")
    @JsonIgnoreProperties({"password", "favoritePlaceHash", "email", "phone", "authorities", "enabled", "accountNonExpired", "accountNonLocked", "credentialsNonExpired", "age", "available", "lastDonationDate", "isDonor", "isPatient", "patientSince"})
    private User requestedBy;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    public enum RequestStatus {
        PENDING, PROCESSING, FULFILLED, CANCELLED, FAILED
    }


    
}

package com.bms.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

@Entity
@Data
@Getter
@Setter
@Builder
@NoArgsConstructor 
@AllArgsConstructor
public class DonorResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private java.util.UUID id;

    @ManyToOne
    @JoinColumn(name = "donor_id", referencedColumnName = "id")
    @JsonIgnoreProperties({"password", "favoritePlaceHash", "email", "phone", "authorities", "enabled", "accountNonExpired", "accountNonLocked", "credentialsNonExpired", "age", "available", "lastDonationDate", "isDonor", "isPatient", "patientSince"})
    private User donor;

    @ManyToOne
    @JoinColumn(name = "request_id", referencedColumnName = "id")
    private BloodRequest request;

    @Enumerated(EnumType.STRING)
    private ResponseStatus response;

    private LocalDateTime responseTime;
    
    // Tracks which batch this notification belongs to
    private Integer batchNumber;

    public enum ResponseStatus {
        PENDING, YES, NO, TIMEOUT
    }


    
}

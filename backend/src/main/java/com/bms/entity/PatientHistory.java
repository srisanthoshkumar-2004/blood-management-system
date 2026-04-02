package com.bms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "patient_histories")
public class PatientHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private java.util.UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonIgnoreProperties({"password", "favoritePlaceHash", "email", "phone", "authorities", "enabled", "accountNonExpired", "accountNonLocked", "credentialsNonExpired", "age", "available", "lastDonationDate", "isDonor", "isPatient", "patientSince"})
    private User patient;

    @Column(nullable = false, length = 1000)
    private String requestDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private BloodRequest bloodRequest;

    @Column(nullable = false)
    private String hospitalName;

    // FULFILLED, PENDING, FAILED
    @Column(nullable = false)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fulfilled_donor_id")
    @JsonIgnoreProperties({"password", "favoritePlaceHash", "email", "phone", "authorities", "enabled", "accountNonExpired", "accountNonLocked", "credentialsNonExpired", "age", "available", "lastDonationDate", "isDonor", "isPatient", "patientSince"})
    private User fulfilledDonor;

    private LocalDateTime requestDate;
}

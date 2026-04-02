package com.bms.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;

@Entity

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor 
@AllArgsConstructor
public class DonationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private java.util.UUID id;

    @ManyToOne
    @JoinColumn(name = "donor_id", referencedColumnName = "id")
    @JsonIgnoreProperties({"password", "favoritePlaceHash", "email", "phone", "authorities", "enabled", "accountNonExpired", "accountNonLocked", "credentialsNonExpired", "age", "available", "lastDonationDate", "isDonor", "isPatient", "patientSince"})
    private User donor;

    @Column(nullable = false)
    private LocalDate donationDate;

    @Column(nullable = false)
    private String bloodGroup;

    @Column(nullable = false)
    private String hospitalName;

    @Column(nullable = false)
    private Integer unitsDonated;
}

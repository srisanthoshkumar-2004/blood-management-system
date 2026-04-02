package com.bms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor 
@AllArgsConstructor

public class DonorHealth {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private java.util.UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(nullable = false)
    private Double weight;

    @Column(nullable = false)
    private Double hemoglobinLevel;

    @Column(nullable = false)
    private String bloodPressure;

    private LocalDate lastCheckupDate;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean isEligible = false;

    private String medicalConditions;

    public String getMedicalConditions() {
        return medicalConditions;
    }

    public void setMedicalConditions(String medicalConditions) {
        this.medicalConditions = medicalConditions;
    }

    @PrePersist
    @PreUpdate
    public void calculateEligibility() {
        if (user == null) {
            this.isEligible = false;
            return;
        }
        
        boolean validAge = user.getAge() != null && user.getAge() >= 18 && user.getAge() <= 60;
        boolean validWeight = this.weight != null && this.weight >= 50.0;
        boolean validHemo = this.hemoglobinLevel != null && this.hemoglobinLevel >= 12.5;
        
        boolean validGap = true;
        if (user.getLastDonationDate() != null) {
            long daysSince = java.time.temporal.ChronoUnit.DAYS.between(user.getLastDonationDate(), LocalDate.now());
            validGap = daysSince >= 90;
        }
        
        this.isEligible = validAge && validWeight && validHemo && validGap && !user.getIsPatient();
    }
}

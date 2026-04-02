package com.bms.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "blood_banks")
public class BloodBank {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private java.util.UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String contactNumber;

    // We can store a comma-separated list of blood groups like "A+,B+,O-"
    // In a more complex production schema this could be a One-to-Many inventory table
    @Column(length = 500)
    private String availableBloodGroups;
}

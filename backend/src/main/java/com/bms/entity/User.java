package com.bms.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.validation.constraints.*;
import java.util.Collection;
import java.util.List;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@Getter
@Setter
@Builder
@NoArgsConstructor 
@AllArgsConstructor
@Table(name = "users")

public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private java.util.UUID id;

    @Column(unique = true, nullable = false)
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @Column(unique = true, nullable = false)
    @NotBlank
    @Email
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    @NotBlank
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Pattern(regexp = "^(A|B|AB|O)[+-]$|^$")
    private String bloodGroup;

    @NotBlank
    private String location;

    @NotBlank
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$")
    private String phone;

    @Min(18)
    private Integer age;

    @Builder.Default
    private Boolean available = true;
    private LocalDate lastDonationDate;
    
    // Lifecycle Tracking
    @Builder.Default
    private Boolean isDonor = false;
    @Builder.Default
    private Boolean isPatient = false;
    private LocalDate patientSince; // Used to track 30 days gap

    // Security Question
    @Column(nullable = false, columnDefinition = "varchar(255) default 'N/A'")
    @JsonIgnore
    private String favoritePlaceHash;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

	
}

package com.bms.repository;

import com.bms.entity.DonorHealth;
import com.bms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DonorHealthRepository extends JpaRepository<DonorHealth, java.util.UUID> {
    Optional<DonorHealth> findByUser(User user);
    Optional<DonorHealth> findByUserId(java.util.UUID userId);
}

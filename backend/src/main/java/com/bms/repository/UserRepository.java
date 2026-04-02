package com.bms.repository;

import com.bms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, java.util.UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u JOIN DonorHealth dh ON dh.user = u " +
           "WHERE u.bloodGroup = :bloodGroup AND u.location = :location " +
           "AND u.available = true AND dh.isEligible = true " +
           "AND u.isDonor = true AND u.isPatient = false " +
           "ORDER BY " +
           "CASE WHEN u.age BETWEEN 18 AND 40 THEN 1 ELSE 2 END ASC, " +
           "u.lastDonationDate ASC")
    List<User> findEligibleDonors(@Param("bloodGroup") String bloodGroup, @Param("location") String location);

    List<User> findByRoleAndLocation(com.bms.entity.Role role, String location);
}

package com.bms.repository;

import com.bms.entity.BloodRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import jakarta.persistence.LockModeType;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

import java.util.List;

public interface BloodRequestRepository extends JpaRepository<BloodRequest, java.util.UUID> {
    List<BloodRequest> findByStatus(BloodRequest.RequestStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM BloodRequest b WHERE b.id = :id")
    Optional<BloodRequest> findByIdWithLock(@Param("id") UUID id);

    List<BloodRequest> findByRequestedByIdOrderByRequestDateDesc(UUID userId);
    List<BloodRequest> findByLocationOrderByRequestDateDesc(String location);
}

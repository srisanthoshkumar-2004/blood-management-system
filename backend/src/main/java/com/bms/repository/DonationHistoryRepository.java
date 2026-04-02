package com.bms.repository;

import com.bms.entity.DonationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DonationHistoryRepository extends JpaRepository<DonationHistory, java.util.UUID> {
    List<DonationHistory> findByDonorIdOrderByDonationDateDesc(java.util.UUID donorId);
}

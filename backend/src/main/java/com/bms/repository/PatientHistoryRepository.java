package com.bms.repository;

import com.bms.entity.PatientHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PatientHistoryRepository extends JpaRepository<PatientHistory, java.util.UUID> {
    List<PatientHistory> findByPatientIdOrderByRequestDateDesc(java.util.UUID patientId);
    java.util.Optional<PatientHistory> findByBloodRequestId(java.util.UUID requestId);
}

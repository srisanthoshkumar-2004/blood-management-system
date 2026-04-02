package com.bms.repository;

import com.bms.entity.DonorResponse;
import com.bms.entity.BloodRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DonorResponseRepository extends JpaRepository<DonorResponse, java.util.UUID> {
    List<DonorResponse> findByRequest(BloodRequest request);
    List<DonorResponse> findByRequestIdAndBatchNumber(java.util.UUID requestId, Integer batchNumber);
    List<DonorResponse> findByRequestIdAndResponse(java.util.UUID requestId, DonorResponse.ResponseStatus response);
    java.util.Optional<DonorResponse> findByRequestIdAndDonorId(java.util.UUID requestId, java.util.UUID donorId);
}

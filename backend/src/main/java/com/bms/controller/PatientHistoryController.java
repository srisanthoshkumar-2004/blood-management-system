package com.bms.controller;

import com.bms.entity.PatientHistory;
import com.bms.repository.PatientHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patient")
@RequiredArgsConstructor
public class PatientHistoryController {

    private final PatientHistoryRepository patientHistoryRepository;

    @GetMapping("/{patientId}/history")
    @PreAuthorize("@ownershipValidator.isSelf(#patientId)")
    public ResponseEntity<List<PatientHistory>> getPatientHistory(@PathVariable java.util.UUID patientId) {
        return ResponseEntity.ok(patientHistoryRepository.findByPatientIdOrderByRequestDateDesc(patientId));
    }
}

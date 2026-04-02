package com.bms.service;

import com.bms.entity.User;
import com.bms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientRecoveryTask {

    private final UserRepository userRepository;

    // Run every day at midnight
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void reinstateRecoveredPatients() {
        log.info("Starting Patient Recovery Job: Checking for patients who finished 30 days gap.");
        
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        
        List<User> recoveringPatients = userRepository.findAll().stream()
                .filter(u -> Boolean.TRUE.equals(u.getIsPatient()))
                .filter(u -> u.getPatientSince() != null) // Only process patients with a valid patientSince date
                .filter(u -> !u.getPatientSince().isAfter(thirtyDaysAgo)) // patientSince is on or before 30 days ago
                .toList();

        int count = 0;
        for (User u : recoveringPatients) {
            log.info("Reinstating User {} to Donor Status after 30 days of patient status.", u.getUsername());
            u.setIsPatient(false);
            u.setIsDonor(true);
            u.setAvailable(true);
            u.setPatientSince(null);
            userRepository.save(u);
            count++;
        }
        
        log.info("Patient Recovery Job Completed. Reinstated {} users.", count);
    }
}

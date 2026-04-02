package com.bms.repository;

import com.bms.entity.BloodBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BloodBankRepository extends JpaRepository<BloodBank, java.util.UUID> {
    
    @Query("SELECT b FROM BloodBank b WHERE b.location LIKE %:location% AND b.availableBloodGroups LIKE %:bloodGroup%")
    List<BloodBank> findAvailableBanks(String location, String bloodGroup);
}

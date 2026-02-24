package com.carrental.repository;
import java.math.BigDecimal;  // For BigDecimal
import java.util.Optional;     // For Optional
import org.springframework.data.repository.query.Param;  // For @Param
import com.carrental.entity.Insurance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InsuranceRepository extends JpaRepository<Insurance, Long> {
    List<Insurance> findByVehicleId(Long vehicleId);
    Optional<Insurance> findByPolicyNumber(String policyNumber);
    
    @Query("SELECT i FROM Insurance i WHERE i.endDate <= :date AND i.active = true")
    List<Insurance> findExpiringInsurances(@Param("date") LocalDate date);
    
    @Query("SELECT i FROM Insurance i WHERE i.active = true")
    List<Insurance> findActiveInsurances();
}
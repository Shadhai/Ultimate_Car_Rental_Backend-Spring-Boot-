package com.carrental.repository;

import com.carrental.entity.DamageReport;
import com.carrental.enums.DamageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DamageReportRepository extends JpaRepository<DamageReport, Long> {
    List<DamageReport> findByRentalId(Long rentalId);
    List<DamageReport> findByStatus(DamageStatus status);
    
    // CHANGE THIS METHOD TO USE JPQL
    @Query("SELECT d FROM DamageReport d WHERE d.rental.vehicle.id = :vehicleId")
    List<DamageReport> findByVehicleId(@Param("vehicleId") Long vehicleId);
}
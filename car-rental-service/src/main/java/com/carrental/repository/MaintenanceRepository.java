package com.carrental.repository;
import java.math.BigDecimal;  // For BigDecimal
import java.util.Optional;     // For Optional
import org.springframework.data.repository.query.Param;  // For @Param
import com.carrental.entity.Maintenance;
import com.carrental.enums.MaintenanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {
    List<Maintenance> findByVehicleId(Long vehicleId);
    List<Maintenance> findByStatus(MaintenanceStatus status);
    
    @Query("SELECT m FROM Maintenance m WHERE m.nextMaintenanceDate <= :date AND m.status != 'COMPLETED'")
    List<Maintenance> findOverdueMaintenance(@Param("date") LocalDate date);
}
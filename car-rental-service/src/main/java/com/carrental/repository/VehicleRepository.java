package com.carrental.repository;

import com.carrental.entity.Vehicle;
import com.carrental.enums.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByRegistrationNumber(String registrationNumber);
    List<Vehicle> findByLocationId(Long locationId);
    List<Vehicle> findByStatus(VehicleStatus status);
    List<Vehicle> findByAvailable(boolean available);
    
    @Query("SELECT COUNT(v) FROM Vehicle v WHERE v.available = true")
    Long countAvailableVehicles();
    
    @Query("SELECT v FROM Vehicle v WHERE v.location.id = :locationId AND v.available = true")
    List<Vehicle> findAvailableVehiclesByLocation(@Param("locationId") Long locationId);
}
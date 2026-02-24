package com.carrental.repository;

import com.carrental.entity.VehicleImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleImageRepository extends JpaRepository<VehicleImage, Long> {
    List<VehicleImage> findByVehicleId(Long vehicleId);
    List<VehicleImage> findByVehicleIdAndPrimary(Long vehicleId, boolean primary);
    
    void deleteByVehicleId(Long vehicleId);
}
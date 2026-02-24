package com.carrental.repository;

import com.carrental.entity.InspectionImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InspectionImageRepository extends JpaRepository<InspectionImage, Long> {
    List<InspectionImage> findByDamageReportId(Long damageReportId);
    List<InspectionImage> findByVehicleId(Long vehicleId);
    List<InspectionImage> findByImageType(String imageType);
}
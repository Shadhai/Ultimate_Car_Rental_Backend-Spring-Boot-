package com.carrental.entity;

import com.carrental.entity.base.BaseEntity;
import com.carrental.enums.VehicleStatus;
import com.carrental.enums.VehicleType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle extends BaseEntity {
    
    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Column(name = "registration_number", unique = true, nullable = false)
    private String registrationNumber;

    @Column(name = "manufacture_year")
    private Integer year;

    @Enumerated(EnumType.STRING)
    private VehicleType type;

    @Enumerated(EnumType.STRING)
    private VehicleStatus status;

    @Column(name = "daily_rate", precision = 10, scale = 2)
    private BigDecimal dailyRate;

    @Column(name = "hourly_rate", precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    private Integer capacity;
    private String color;
    private String fuelType;

    @Column(columnDefinition = "TEXT")
    private String features;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @Column(name = "is_available")
    private boolean available = true;

    @Column(name = "insurance_number")
    private String insuranceNumber;

    @Column(name = "last_maintenance_date")
    private java.time.LocalDate lastMaintenanceDate;
    
    // ADD THIS RELATIONSHIP
    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VehicleImage> vehicleImages = new ArrayList<>();
    
    // ADD THIS GETTER METHOD
    public List<VehicleImage> getVehicleImages() {
        return vehicleImages;
    }
    
    // ADD THIS HELPER METHOD TO ADD IMAGE
    public void addVehicleImage(VehicleImage image) {
        vehicleImages.add(image);
        image.setVehicle(this);
    }
    
    // ADD THIS HELPER METHOD TO REMOVE IMAGE
    public void removeVehicleImage(VehicleImage image) {
        vehicleImages.remove(image);
        image.setVehicle(null);
    }
    
    // ADD THIS METHOD TO GET PRIMARY IMAGE URL
    public String getPrimaryImageUrl() {
        return vehicleImages.stream()
                .filter(VehicleImage::isPrimary)
                .map(VehicleImage::getImageUrl)
                .findFirst()
                .orElse(vehicleImages.isEmpty() ? null : vehicleImages.get(0).getImageUrl());
    }
}
package com.carrental.dto.response;

import com.carrental.enums.VehicleStatus;
import com.carrental.enums.VehicleType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class VehicleResponseDto {
    private Long id;
    private String brand;
    private String model;
    private String registrationNumber;
    private Integer year;
    private VehicleType type;
    private VehicleStatus status;
    private BigDecimal dailyRate;
    private BigDecimal hourlyRate;
    private Integer capacity;
    private String color;
    private String fuelType;
    private String features;
    private Long locationId;
    private String locationName;
    private boolean available;
    private String insuranceNumber;
    private String lastMaintenanceDate;
    private List<String> imageUrls;
    private String createdAt;
}
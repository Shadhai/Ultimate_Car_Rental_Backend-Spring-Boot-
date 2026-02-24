package com.carrental.dto.request;

import com.carrental.enums.VehicleStatus;
import com.carrental.enums.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class VehicleRequestDto {
    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Model is required")
    private String model;

    @NotBlank(message = "Registration number is required")
    private String registrationNumber;

    @NotNull(message = "Year is required")
    @Positive(message = "Year must be positive")
    private Integer year;

    @NotNull(message = "Vehicle type is required")
    private VehicleType type;

    @NotNull(message = "Status is required")
    private VehicleStatus status;

    @NotNull(message = "Daily rate is required")
    @Positive(message = "Daily rate must be positive")
    private BigDecimal dailyRate;

    private BigDecimal hourlyRate;
    
    @NotNull(message = "Location ID is required")
    private Long locationId;

    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be positive")
    private Integer capacity;

    private String color;
    private String fuelType;
    private String features;
    private String insuranceNumber;
}
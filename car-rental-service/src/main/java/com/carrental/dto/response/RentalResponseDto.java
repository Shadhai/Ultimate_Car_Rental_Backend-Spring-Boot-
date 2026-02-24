package com.carrental.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RentalResponseDto {
    private Long id;
    private Long customerId;
    private String customerName;
    private Long vehicleId;
    private String vehicleDetails;
    private Long driverId;
    private String driverName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime actualStartTime;
    private LocalDateTime actualEndTime;
    private BigDecimal totalFare;
    private BigDecimal platformCommission;
    private BigDecimal driverAmount;
    private String status;
    private String notes;
    private String pickupLocation;
    private String dropoffLocation;
    private boolean paid;
    private String paymentStatus;
    private String createdAt;
    private String updatedAt;
}
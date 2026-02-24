package com.carrental.dto.response;

import com.carrental.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverStatusDto {
    private Long driverId;
    private String driverName;
    private UserStatus status;
    private boolean isActive;
    private LocalDateTime lastActive;
    private String currentLocation;
    private boolean isAvailable;
    private LocalDateTime onlineSince;
    private Integer totalTripsToday;
    private Integer totalEarningsToday;
}
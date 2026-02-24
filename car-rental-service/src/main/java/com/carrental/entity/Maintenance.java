package com.carrental.entity;

import com.carrental.entity.base.BaseEntity;
import com.carrental.enums.MaintenanceStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "maintenances")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Maintenance extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(name = "maintenance_date", nullable = false)
    private LocalDate maintenanceDate;

    @Column(name = "next_maintenance_date")
    private LocalDate nextMaintenanceDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    private BigDecimal cost;

    @Column(name = "service_center")
    private String serviceCenter;

    @Enumerated(EnumType.STRING)
    private MaintenanceStatus status;

    @Column(name = "odometer_reading")
    private Integer odometerReading;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
package com.carrental.entity;

import com.carrental.entity.base.BaseEntity;
import com.carrental.enums.RentalStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rentals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rental extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private User driver;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "actual_start_time")
    private LocalDateTime actualStartTime;

    @Column(name = "actual_end_time")
    private LocalDateTime actualEndTime;

    @Column(name = "total_fare", precision = 10, scale = 2)
    private BigDecimal totalFare;

    @Column(name = "platform_commission", precision = 10, scale = 2)
    private BigDecimal platformCommission;

    @Column(name = "driver_amount", precision = 10, scale = 2)
    private BigDecimal driverAmount;

    @Enumerated(EnumType.STRING)
    private RentalStatus status;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "pickup_location")
    private String pickupLocation;

    @Column(name = "dropoff_location")
    private String dropoffLocation;

    @Column(name = "is_paid")
    private boolean paid = false;

    @OneToOne(mappedBy = "rental", cascade = CascadeType.ALL)
    private Payment payment;
}
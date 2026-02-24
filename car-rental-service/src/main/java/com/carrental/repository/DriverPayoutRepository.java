package com.carrental.repository;
import java.math.BigDecimal;  // For BigDecimal
import java.util.Optional;     // For Optional
import org.springframework.data.repository.query.Param;  // For @Param
import com.carrental.entity.DriverPayout;
import com.carrental.enums.PayoutStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DriverPayoutRepository extends JpaRepository<DriverPayout, Long> {
    List<DriverPayout> findByDriverId(Long driverId);
    List<DriverPayout> findByStatus(PayoutStatus status);
    
    @Query("SELECT dp FROM DriverPayout dp WHERE dp.driver.id = :driverId AND dp.payoutDate BETWEEN :startDate AND :endDate")
    List<DriverPayout> findByDriverIdAndDateRange(@Param("driverId") Long driverId,
                                                  @Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(dp.driverAmount) FROM DriverPayout dp WHERE dp.status = 'PAID'")
    BigDecimal getTotalPaidAmount();
    
    @Query("SELECT SUM(dp.driverAmount) FROM DriverPayout dp WHERE dp.status = 'PENDING'")
    BigDecimal getTotalPendingAmount();
}
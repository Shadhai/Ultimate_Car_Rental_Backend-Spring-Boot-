package com.carrental.repository;

import com.carrental.entity.Rental;
import com.carrental.enums.RentalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findByCustomerId(Long customerId);
    List<Rental> findByDriverId(Long driverId);
    List<Rental> findByVehicleId(Long vehicleId);
    List<Rental> findByStatus(RentalStatus status);
 // In RentalRepository.java, add:
    @Query("SELECT r FROM Rental r WHERE r.vehicle.location.id = :locationId")
    List<Rental> findByVehicleLocationId(@Param("locationId") Long locationId);

    @Query("SELECT r FROM Rental r WHERE r.vehicle.location.id = :locationId AND r.status = :status")
    List<Rental> findByVehicleLocationIdAndStatus(@Param("locationId") Long locationId, 
                                                  @Param("status") RentalStatus status);
    
    @Query("SELECT COUNT(r) FROM Rental r WHERE r.status = :status")
    Long countByStatus(@Param("status") RentalStatus status);
    
    @Query("SELECT r FROM Rental r WHERE r.vehicle.id = :vehicleId AND " +
           "((r.startTime <= :endTime AND r.endTime >= :startTime) OR " +
           "(r.actualStartTime <= :endTime AND r.actualEndTime >= :startTime)) AND " +
           "r.status NOT IN ('CANCELLED', 'COMPLETED')")
    List<Rental> findOverlappingRentals(@Param("vehicleId") Long vehicleId,
                                        @Param("startTime") LocalDateTime startTime,
                                        @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT r FROM Rental r WHERE r.startTime >= :startDate AND r.startTime <= :endDate")
    List<Rental> findRentalsBetweenDates(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(r.totalFare) FROM Rental r WHERE r.status = 'COMPLETED'")
    BigDecimal getTotalRevenue();
    
    // Add this method
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END FROM Rental r WHERE r.vehicle.id = :vehicleId AND r.status IN :statuses")
    boolean existsByVehicleIdAndStatusIn(@Param("vehicleId") Long vehicleId, 
                                         @Param("statuses") List<RentalStatus> statuses);
    
}
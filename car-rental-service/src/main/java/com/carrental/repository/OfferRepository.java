package com.carrental.repository;

import com.carrental.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
    Optional<Offer> findByCouponCode(String couponCode);
    List<Offer> findByActive(boolean active);
    
    @Query("SELECT o FROM Offer o WHERE o.active = true AND o.startDate <= :currentDate AND o.endDate >= :currentDate")
    List<Offer> findActiveOffers(@Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT o FROM Offer o WHERE o.couponCode = :couponCode AND o.active = true " +
           "AND o.startDate <= :currentDate AND o.endDate >= :currentDate " +
           "AND (o.usageLimit IS NULL OR o.usedCount < o.usageLimit)")
    Optional<Offer> findValidOffer(@Param("couponCode") String couponCode, 
                                   @Param("currentDate") LocalDateTime currentDate);
}
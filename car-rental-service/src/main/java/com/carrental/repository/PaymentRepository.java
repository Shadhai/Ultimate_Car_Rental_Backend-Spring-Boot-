package com.carrental.repository;

import com.carrental.entity.Payment;
import com.carrental.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByStatus(PaymentStatus status);
    Optional<Payment> findByRentalId(Long rentalId);
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'SUCCESS'")
    BigDecimal getTotalRevenue();
    List<Payment> findByPaymentDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}
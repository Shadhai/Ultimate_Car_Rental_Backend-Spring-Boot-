package com.carrental.dto.response;

import com.carrental.enums.PaymentMethod;
import com.carrental.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDto {
    private Long id;
    private Long rentalId;
    private BigDecimal amount;
    private PaymentMethod method;
    private PaymentStatus status;
    private String transactionId;
    private String gatewayResponse;
    private String paymentDate;  // Change from LocalDateTime to String
    private String createdAt;    // Change from LocalDateTime to String
}
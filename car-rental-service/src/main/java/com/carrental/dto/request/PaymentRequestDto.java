package com.carrental.dto.request;

import com.carrental.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequestDto {
    @NotNull(message = "Rental ID is required")
    private Long rentalId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Payment method is required")
    private PaymentMethod method;

    private String transactionId;
    private String cardNumber;
    private String cardHolderName;
    private String cardExpiry;
    private String cardCvv;
    private String upiId;
}
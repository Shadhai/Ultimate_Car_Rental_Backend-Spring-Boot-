package com.carrental.service.customer;

import com.carrental.dto.request.PaymentRequestDto;
import com.carrental.dto.response.PaymentResponseDto;
import com.carrental.entity.Payment;
import com.carrental.entity.Rental;
import com.carrental.enums.PaymentMethod;
import com.carrental.enums.PaymentStatus;
import com.carrental.enums.RentalStatus;
import com.carrental.exception.BadRequestException;
import com.carrental.exception.ResourceNotFoundException;
import com.carrental.mapper.PaymentMapper;
import com.carrental.repository.PaymentRepository;
import com.carrental.repository.RentalRepository;
import com.carrental.service.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CustomerPaymentService extends BaseService<Payment, Long> {
    
    private final PaymentRepository paymentRepository;
    private final RentalRepository rentalRepository;
    private final PaymentMapper paymentMapper;
    
    @Override
    protected PaymentRepository getRepository() {
        return paymentRepository;
    }
    
    @Override
    protected String getEntityName() {
        return "Payment";
    }
    
    @Transactional
    public PaymentResponseDto processPayment(Long customerId, PaymentRequestDto request) {
        // Validate rental
        Rental rental = rentalRepository.findById(request.getRentalId())
                .orElseThrow(() -> new ResourceNotFoundException("Rental not found"));
        
        // Check authorization
        if (!rental.getCustomer().getId().equals(customerId)) {
            throw new BadRequestException("You are not authorized to pay for this rental");
        }
        
        // Check rental status
        if (rental.getStatus() != RentalStatus.PENDING && 
            rental.getStatus() != RentalStatus.CONFIRMED) {
            throw new BadRequestException("Payment cannot be processed for this rental status");
        }
        
        // Check if already paid
        if (rental.isPaid()) {
            throw new BadRequestException("Rental is already paid");
        }
        
        // Validate payment amount
        if (request.getAmount().compareTo(rental.getTotalFare()) != 0) {
            throw new BadRequestException("Payment amount must equal total fare: " + rental.getTotalFare());
        }
        
        // Process payment based on method
        PaymentStatus paymentStatus = PaymentStatus.SUCCESS;
        String transactionId = request.getTransactionId();
        String gatewayResponse = "Payment successful";
        
        if (request.getMethod() == PaymentMethod.CASH) {
            // For cash payments, mark as pending and require manual confirmation
            paymentStatus = PaymentStatus.PENDING;
            gatewayResponse = "Awaiting cash payment confirmation";
        } else {
            // For online payments, integrate with payment gateway
            try {
                // Simulate payment gateway integration
                transactionId = "TXN" + System.currentTimeMillis();
                gatewayResponse = processOnlinePayment(request);
            } catch (Exception e) {
                paymentStatus = PaymentStatus.FAILED;
                gatewayResponse = "Payment failed: " + e.getMessage();
            }
        }
        
        // Create payment record
        Payment payment = Payment.builder()
                .rental(rental)
                .amount(request.getAmount())
                .method(request.getMethod())
                .status(paymentStatus)
                .transactionId(transactionId)
                .paymentDate(LocalDateTime.now())
                .gatewayResponse(gatewayResponse)
                .build();
        
        Payment savedPayment = paymentRepository.save(payment);
        
        // Update rental status
        if (paymentStatus == PaymentStatus.SUCCESS) {
            rental.setPaid(true);
            rental.setStatus(RentalStatus.CONFIRMED);
            rentalRepository.save(rental);
        }
        
        return paymentMapper.toResponseDto(savedPayment);
    }
    
    private String processOnlinePayment(PaymentRequestDto request) {
        // This is a mock implementation
        // In production, integrate with actual payment gateway like Stripe, Razorpay, etc.
        
        // Validate card details for card payments
        if (request.getMethod() == PaymentMethod.CREDIT_CARD || 
            request.getMethod() == PaymentMethod.DEBIT_CARD) {
            if (request.getCardNumber() == null || request.getCardNumber().length() < 16) {
                throw new BadRequestException("Invalid card number");
            }
            if (request.getCardExpiry() == null) {
                throw new BadRequestException("Card expiry required");
            }
            if (request.getCardCvv() == null || request.getCardCvv().length() < 3) {
                throw new BadRequestException("CVV required");
            }
        }
        
        // For UPI payments
        if (request.getMethod() == PaymentMethod.UPI) {
            if (request.getUpiId() == null || !request.getUpiId().contains("@")) {
                throw new BadRequestException("Invalid UPI ID");
            }
        }
        
        // Simulate random failure (5% chance for testing)
        if (Math.random() < 0.05) {
            throw new RuntimeException("Payment gateway timeout");
        }
        
        return "{\"status\":\"success\",\"message\":\"Payment processed successfully\"}";
    }
    
    public PaymentResponseDto getPaymentByRentalId(Long rentalId, Long customerId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new ResourceNotFoundException("Rental not found"));
        
        // Check authorization
        if (!rental.getCustomer().getId().equals(customerId)) {
            throw new BadRequestException("You are not authorized to view this payment");
        }
        
        Payment payment = paymentRepository.findByRentalId(rentalId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        
        return paymentMapper.toResponseDto(payment);
    }
    
    @Transactional
    public PaymentResponseDto refundPayment(Long paymentId, Long customerId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        
        Rental rental = payment.getRental();
        
        // Check authorization
        if (!rental.getCustomer().getId().equals(customerId)) {
            throw new BadRequestException("You are not authorized to request refund");
        }
        
        // Check if refund is possible
        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new BadRequestException("Only successful payments can be refunded");
        }
        
        if (rental.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Cannot refund after rental start time");
        }
        
        // Process refund
        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setGatewayResponse(payment.getGatewayResponse() + " | Refund processed");
        Payment updatedPayment = paymentRepository.save(payment);
        
        // Update rental status
        rental.setPaid(false);
        rental.setStatus(RentalStatus.CANCELLED);
        rentalRepository.save(rental);
        
        return paymentMapper.toResponseDto(updatedPayment);
    }
}
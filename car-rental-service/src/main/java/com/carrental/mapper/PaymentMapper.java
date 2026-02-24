package com.carrental.mapper;

import com.carrental.dto.response.PaymentResponseDto;
import com.carrental.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "rentalId", source = "rental.id")
    @Mapping(target = "paymentDate", source = "paymentDate", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "localDateTimeToString")
    PaymentResponseDto toResponseDto(Payment payment);

    List<PaymentResponseDto> toResponseDtoList(List<Payment> payments);

    @Named("localDateTimeToString")
    default String localDateTimeToString(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toString() : null;
    }
}
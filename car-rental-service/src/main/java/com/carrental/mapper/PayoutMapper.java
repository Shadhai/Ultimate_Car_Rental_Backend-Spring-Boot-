package com.carrental.mapper;

import com.carrental.dto.response.DriverPayoutResponseDto;
import com.carrental.entity.DriverPayout;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PayoutMapper {

    @Mapping(target = "driverId", source = "driver.id")
    @Mapping(target = "driverName", source = "driver.name")
    @Mapping(target = "rentalId", source = "rental.id")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "payoutDate", source = "payoutDate", qualifiedByName = "localDateTimeToString")
    DriverPayoutResponseDto toResponseDto(DriverPayout payout);

    List<DriverPayoutResponseDto> toResponseDtoList(List<DriverPayout> payouts);

    @Named("localDateTimeToString")
    default String localDateTimeToString(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toString() : null;
    }
}
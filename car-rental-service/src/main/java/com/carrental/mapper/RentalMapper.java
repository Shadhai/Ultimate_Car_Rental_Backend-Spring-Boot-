package com.carrental.mapper;

import com.carrental.dto.response.RentalResponseDto;
import com.carrental.entity.Rental;
import com.carrental.entity.User;
import com.carrental.entity.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring")
public interface RentalMapper {

    DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", source = "customer.name")
    @Mapping(target = "driverId", source = "driver.id")
    @Mapping(target = "driverName", source = "driver.name")
    @Mapping(target = "vehicleId", source = "vehicle.id")
    @Mapping(target = "vehicleDetails", source = "vehicle", qualifiedByName = "mapVehicleDetails")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "paymentStatus", source = "payment.status")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "formatDateTime")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "formatDateTime")
    RentalResponseDto toResponseDto(Rental rental);

    List<RentalResponseDto> toResponseDtoList(List<Rental> rentals);

    @Named("mapVehicleDetails")
    default String mapVehicleDetails(Vehicle vehicle) {
        if (vehicle == null) {
            return null;
        }
        StringBuilder details = new StringBuilder();
        if (vehicle.getBrand() != null) {
            details.append(vehicle.getBrand());
        }
        if (vehicle.getModel() != null) {
            if (details.length() > 0)
                details.append(" ");
            details.append(vehicle.getModel());
        }
        if (vehicle.getRegistrationNumber() != null) {
            details.append(" (").append(vehicle.getRegistrationNumber()).append(")");
        }
        return details.toString();
    }

    @Named("formatDateTime")
    default String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_FORMATTER) : null;
    }
}
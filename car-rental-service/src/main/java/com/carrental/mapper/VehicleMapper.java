package com.carrental.mapper;

import com.carrental.dto.response.VehicleResponseDto;
import com.carrental.entity.Vehicle;
import com.carrental.entity.VehicleImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Mapping(target = "locationId", source = "location.id")
    @Mapping(target = "locationName", source = "location.name")
    @Mapping(target = "lastMaintenanceDate", source = "lastMaintenanceDate", qualifiedByName = "formatDateOnly")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "formatDateTime")
    @Mapping(target = "imageUrls", source = "vehicleImages", qualifiedByName = "extractImageUrls")
    VehicleResponseDto toResponseDto(Vehicle vehicle);

    List<VehicleResponseDto> toResponseDtoList(List<Vehicle> vehicles);

    @Named("formatDateOnly")
    default String formatDateOnly(java.time.LocalDate date) {
        return date != null ? date.format(DATE_ONLY_FORMATTER) : null;
    }

    @Named("formatDateTime")
    default String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_FORMATTER) : null;
    }

    @Named("extractImageUrls")
    default List<String> extractImageUrls(List<VehicleImage> vehicleImages) {
        if (vehicleImages == null || vehicleImages.isEmpty()) {
            return List.of();
        }
        return vehicleImages.stream()
                .map(VehicleImage::getImageUrl)
                .collect(Collectors.toList());
    }
}
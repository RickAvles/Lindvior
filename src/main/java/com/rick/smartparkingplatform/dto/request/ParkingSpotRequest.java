package com.rick.smartparkingplatform.dto.request;

import com.rick.smartparkingplatform.enums.ParkingSpotType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ParkingSpotRequest(

        @NotBlank
        String code,

        @NotNull
        UUID parkingSectorId,

        @NotNull
        ParkingSpotType type

) {
}
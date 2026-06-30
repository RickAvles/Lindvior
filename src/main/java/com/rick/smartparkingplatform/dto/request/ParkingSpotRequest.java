package com.rick.smartparkingplatform.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ParkingSpotRequest(
        @NotBlank
        String code,

        @NotBlank
        String sector,

        @NotNull
        Integer floor
) {
}

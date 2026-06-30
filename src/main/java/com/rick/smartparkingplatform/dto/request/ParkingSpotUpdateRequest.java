package com.rick.smartparkingplatform.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ParkingSpotUpdateRequest(
        @NotBlank
        String sector,

        @NotNull
        @Min(-5)
        @Max(10)
        Integer floor
) {
}

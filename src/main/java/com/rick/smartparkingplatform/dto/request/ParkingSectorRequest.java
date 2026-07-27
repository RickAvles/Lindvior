package com.rick.smartparkingplatform.dto.request;

import com.rick.smartparkingplatform.enums.ParkingSectorType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ParkingSectorRequest(

        @NotBlank
        String name,

        @NotNull
        ParkingSectorType type,

        @NotNull
        @Min(-5)
        @Max(10)
        Integer floor

) {
}
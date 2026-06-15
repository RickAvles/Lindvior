package com.rick.smartparkingplatform.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ParkingRequest(

        @NotBlank
        String name,

        @NotBlank
        String address,

        @NotNull
        @Positive
        Integer capacity

) {
}
package com.rick.smartparkingplatform.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ParkingRequest(

        @NotBlank
        String name,

        @NotBlank
        String address
) {
}
package com.rick.smartparkingplatform.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ParkingSessionRequest(
        @NotBlank
        String licensePlate
) {
}

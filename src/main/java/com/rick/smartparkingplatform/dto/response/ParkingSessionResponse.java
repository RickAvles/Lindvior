package com.rick.smartparkingplatform.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rick.smartparkingplatform.enums.StatusParkingSession;
import com.rick.smartparkingplatform.enums.VehicleType;

import java.time.LocalDateTime;
import java.util.UUID;

public record ParkingSessionResponse(

        UUID id,

        String licensePlate,

        VehicleType vehicleType,

        LocalDateTime entryTime,

        LocalDateTime exitTime,

        StatusParkingSession status,

        String parkingSpotCode,

        @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime createdAt
) {
}

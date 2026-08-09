package com.rick.smartparkingplatform.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rick.smartparkingplatform.enums.VehicleType;

import java.time.LocalDateTime;
import java.util.UUID;

public record VehicleResponse(

        UUID id,

        String licensePlate,

        VehicleType type,

        String color,

        boolean pcd,

        boolean active,

        @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime createdAt

) {
}
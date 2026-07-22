package com.rick.smartparkingplatform.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rick.smartparkingplatform.enums.ParkingSectorType;
import com.rick.smartparkingplatform.enums.ParkingSpotType;
import com.rick.smartparkingplatform.enums.StatusParkingSpot;

import java.time.LocalDateTime;
import java.util.UUID;

public record ParkingSpotResponse(

        UUID id,

        String code,

        String sectorName,

        ParkingSectorType parkingSectorType,

        ParkingSpotType parkingSpotType,

        Integer floor,

        StatusParkingSpot status,

        boolean active,

        @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime createdAt

) {
}
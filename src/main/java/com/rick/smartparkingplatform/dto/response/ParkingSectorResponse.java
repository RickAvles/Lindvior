package com.rick.smartparkingplatform.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rick.smartparkingplatform.enums.SectorType;

import java.time.LocalDateTime;
import java.util.UUID;

public record ParkingSectorResponse(

        UUID id,

        String name,

        SectorType type,

        Integer floor,

        boolean active,

        @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime createdAt

) {
}
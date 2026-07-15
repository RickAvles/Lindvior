package com.rick.smartparkingplatform.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public record ParkingResponse(

        UUID id,

        String name,

        String address,

        Integer capacity,

        boolean active,

        @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime createdAt,

        @JsonFormat(pattern = "HH:mm:ss")
        LocalTime openingTime,

        @JsonFormat(pattern = "HH:mm:ss")
        LocalTime closingTime

) {
}
package com.rick.smartparkingplatform.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.UUID;

public record ParkingResponse(

        UUID id,

        String name,

        String address,

        Integer capacity,

        Boolean active,

        @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime createdAt

) {
}
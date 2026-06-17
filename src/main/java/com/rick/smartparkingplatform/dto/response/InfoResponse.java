package com.rick.smartparkingplatform.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record InfoResponse(
        String application,
        String version,
        String environment,
        @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime timestamp) {
}

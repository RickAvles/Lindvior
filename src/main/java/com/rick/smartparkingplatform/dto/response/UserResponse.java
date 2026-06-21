package com.rick.smartparkingplatform.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rick.smartparkingplatform.enums.Role;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        Role role,
        boolean active,
        @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime createdAt
) {
}

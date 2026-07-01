package com.rick.smartparkingplatform.dto.filter;

import com.rick.smartparkingplatform.enums.StatusParkingSession;

import java.time.LocalDateTime;

public record ParkingSessionFilter(
        String licensePlate,
        StatusParkingSession status,
        String parkingSpotCode,
        LocalDateTime startDate,
        LocalDateTime endDate
) {
}

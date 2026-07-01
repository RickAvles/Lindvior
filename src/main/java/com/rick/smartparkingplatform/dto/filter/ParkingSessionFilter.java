package com.rick.smartparkingplatform.dto.filter;

import com.rick.smartparkingplatform.enums.StatusParkingSession;

public record ParkingSessionFilter(
        String licensePlate,
        StatusParkingSession status
) {
}

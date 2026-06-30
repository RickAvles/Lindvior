package com.rick.smartparkingplatform.dto.request;

import com.rick.smartparkingplatform.enums.StatusParkingSpot;

public record ParkingSpotFilter(
        String sector,
        Integer floor,
        StatusParkingSpot status,
        Boolean active
) {
}

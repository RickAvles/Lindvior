package com.rick.smartparkingplatform.dto.filter;

import com.rick.smartparkingplatform.enums.StatusParkingSpot;

public record ParkingSpotFilter(
        String sector,
        Integer floor,
        StatusParkingSpot status,
        Boolean active
) {
}

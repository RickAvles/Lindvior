package com.rick.smartparkingplatform.dto.response;

import java.math.BigDecimal;

public record OccupancyResponse(
        Long totalSpots,
        Long availableSpots,
        Long occupiedSpots,
        BigDecimal occupancyRate
) {
}

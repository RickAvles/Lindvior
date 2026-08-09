package com.rick.smartparkingplatform.simulation.metrics.statistics;

import java.math.BigDecimal;

public record ParkingOccupancy(
        long totalSpots,
        long availableSpots,
        long occupiedSpots,
        long reservedSpots,
        BigDecimal occupancyRate
) {
}
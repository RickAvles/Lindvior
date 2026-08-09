package com.rick.smartparkingplatform.dto.response;

import java.time.LocalDateTime;

public record OccupancyReport(
        long totalSpots,
        double averageOccupied,
        long maximumOccupied,
        long minimumOccupied,
        double averageOccupancyRate,
        double maximumOccupancyRate,
        double minimumOccupancyRate,
        LocalDateTime peakTime
) {
}
package com.rick.smartparkingplatform.simulation.dashboard.model;

import java.math.BigDecimal;

public record DashboardParking(

        Long totalSpots,
        Long availableSpots,
        Long occupiedSpots,
        BigDecimal occupancyRate,

        Integer entryQueue,
        Integer parkingQueue,
        Integer exitQueue,

        Long activeSessions,
        Long enteringSessions,
        Long exitingSessions

) {
}
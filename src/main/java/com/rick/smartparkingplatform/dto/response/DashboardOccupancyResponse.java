package com.rick.smartparkingplatform.dto.response;


import java.math.BigDecimal;

public record DashboardOccupancyResponse(

        Long totalSpots,

        Long availableSpots,

        Long occupiedSpots,

        BigDecimal occupancyRate,

        Integer entryQueue,

        Integer parkingQueue,

        Integer exitQueue

) {
}
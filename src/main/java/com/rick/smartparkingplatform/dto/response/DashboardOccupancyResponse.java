package com.rick.smartparkingplatform.dto.response;


import java.math.BigDecimal;
import java.util.List;

public record DashboardOccupancyResponse(

        Long totalSpots,

        Long availableSpots,

        Long occupiedSpots,

        BigDecimal occupancyRate,

        Integer entryQueue,

        Integer parkingQueue,

        Integer exitQueue,

        List<GateResponse> entryGates,

        List<GateResponse> exitGates

) {
}
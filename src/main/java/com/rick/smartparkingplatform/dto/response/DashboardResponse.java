package com.rick.smartparkingplatform.dto.response;

public record DashboardResponse(

        DashboardSimulationResponse simulation,

        DashboardOccupancyResponse occupancy

) {
}
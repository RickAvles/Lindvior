package com.rick.smartparkingplatform.dto.response;

import com.rick.smartparkingplatform.simulation.enums.SimulationState;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DashboardResponse(

        DashboardSimulationResponse simulation,

        DashboardOccupancyResponse occupancy

) {
}
package com.rick.smartparkingplatform.dto.response;

import com.rick.smartparkingplatform.simulation.enums.SimulationState;

import java.time.LocalDateTime;

public record DashboardSimulationResponse(

        LocalDateTime currentTime,

        SimulationState simulationState

) {
}
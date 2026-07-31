package com.rick.smartparkingplatform.simulation.dashboard.model;

import com.rick.smartparkingplatform.simulation.operation.SimulationState;

import java.time.LocalDateTime;

public record DashboardClock(

        LocalDateTime currentTime,
        SimulationState simulationState

) {
}
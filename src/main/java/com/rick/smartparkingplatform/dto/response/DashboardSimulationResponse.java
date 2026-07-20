package com.rick.smartparkingplatform.dto.response;

import com.rick.smartparkingplatform.simulation.conditions.calendar.CalendarDayType;
import com.rick.smartparkingplatform.simulation.conditions.weather.WeatherType;
import com.rick.smartparkingplatform.simulation.operation.SimulationState;

import java.time.LocalDateTime;

public record DashboardSimulationResponse(
        LocalDateTime currentTime,
        SimulationState simulationState,
        CalendarDayType dayType,
        WeatherType weather
) {
}
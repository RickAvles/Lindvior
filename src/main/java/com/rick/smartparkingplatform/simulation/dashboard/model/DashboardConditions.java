package com.rick.smartparkingplatform.simulation.dashboard.model;

import com.rick.smartparkingplatform.simulation.conditions.calendar.CalendarDayType;
import com.rick.smartparkingplatform.simulation.conditions.weather.WeatherType;

public record DashboardConditions(
        WeatherType weather,
        CalendarDayType dayType
) {
}
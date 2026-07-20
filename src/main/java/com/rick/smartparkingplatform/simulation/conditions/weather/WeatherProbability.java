package com.rick.smartparkingplatform.simulation.conditions.weather;

public record WeatherProbability(
        WeatherType weather,
        int probability
) {
}
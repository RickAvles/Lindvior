package com.rick.smartparkingplatform.simulation.conditions.weather;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Getter
@Setter
@Component
public class WeatherContext {

    private WeatherType currentWeather;

    private WeatherType activeProfile;

    private WeatherType pendingProfile;

    private LocalDateTime profileActivationTime;

    private LocalDateTime nextWeatherChange;

}
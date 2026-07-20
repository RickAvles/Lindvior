package com.rick.smartparkingplatform.simulation.conditions.weather;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class WeatherProfileRules {

    private final Map<WeatherType, List<WeatherProbability>> profiles = Map.of(
            WeatherType.SUNNY,
            List.of(
                    new WeatherProbability(WeatherType.SUNNY, 70),
                    new WeatherProbability(WeatherType.CLOUDY, 25),
                    new WeatherProbability(WeatherType.RAINY, 5)
            ),

            WeatherType.CLOUDY,
            List.of(
                    new WeatherProbability(WeatherType.SUNNY, 20),
                    new WeatherProbability(WeatherType.CLOUDY, 60),
                    new WeatherProbability(WeatherType.RAINY, 20)
            ),

            WeatherType.RAINY,
            List.of(
                    new WeatherProbability(WeatherType.SUNNY, 5),
                    new WeatherProbability(WeatherType.CLOUDY, 35),
                    new WeatherProbability(WeatherType.RAINY, 60)
            )
    );

    public List<WeatherProbability> getProfile(WeatherType weatherType) {
        return profiles.get(weatherType);
    }

}
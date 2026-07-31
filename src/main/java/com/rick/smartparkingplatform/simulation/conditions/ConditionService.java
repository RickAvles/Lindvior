package com.rick.smartparkingplatform.simulation.conditions;

import com.rick.smartparkingplatform.service.DashboardStateService;
import com.rick.smartparkingplatform.simulation.conditions.calendar.CalendarDayType;
import com.rick.smartparkingplatform.simulation.conditions.calendar.SimulationCalendarService;
import com.rick.smartparkingplatform.simulation.conditions.weather.SimulationWeatherService;
import com.rick.smartparkingplatform.simulation.conditions.weather.WeatherType;
import com.rick.smartparkingplatform.simulation.dashboard.model.DashboardConditions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConditionService {

    private final SimulationCalendarService calendarService;
    private final SimulationWeatherService weatherService;
    private final DashboardStateService dashboardStateService;

    // Inicializa todas as condições da simulação.
    public void initialize() {
        weatherService.initialize();

        dashboardStateService.updateConditions(
                new DashboardConditions(
                        getCurrentWeather(),
                        getCurrentDayType()
                )
        );
    }

    // Atualiza todas as condições da simulação.
    public void update() {
        weatherService.update();

        dashboardStateService.updateConditions(
                new DashboardConditions(
                        getCurrentWeather(),
                        getCurrentDayType()
                )
        );
    }

    // Retorna o tipo do dia atual.
    public CalendarDayType getCurrentDayType() {
        return calendarService.getCurrentDayType();
    }

    // Retorna o clima atual.
    public WeatherType getCurrentWeather() {
        return weatherService.getCurrentWeather();
    }

}
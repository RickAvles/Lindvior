package com.rick.smartparkingplatform.simulation.conditions.calendar;

import com.rick.smartparkingplatform.simulation.engine.SimulationClock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;


@Service
@RequiredArgsConstructor
public class SimulationCalendarService {

    private final SimulationClock simulationClock;

    /**
     * Obtém o tipo do dia atual da simulação.
     */
    public CalendarDayType getCurrentDayType() {

        DayOfWeek dayOfWeek = simulationClock.getCurrentTime().getDayOfWeek();

        return switch (dayOfWeek) {
            case SATURDAY -> CalendarDayType.SATURDAY;
            case SUNDAY -> CalendarDayType.SUNDAY;
            default -> CalendarDayType.WEEKDAY;
        };
    }

}
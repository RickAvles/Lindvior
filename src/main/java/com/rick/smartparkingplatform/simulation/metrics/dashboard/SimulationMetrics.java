package com.rick.smartparkingplatform.simulation.metrics.dashboard;

import com.rick.smartparkingplatform.simulation.conditions.calendar.CalendarDayType;
import com.rick.smartparkingplatform.simulation.conditions.weather.WeatherType;
import com.rick.smartparkingplatform.simulation.metrics.statistics.GateMetrics;
import com.rick.smartparkingplatform.simulation.operation.SimulationState;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public record SimulationMetrics(

        // Tempo da simulação.
        LocalDateTime currentTime,

        // Estado operacional.
        SimulationState simulationState,

        // Condições ambientais.
        CalendarDayType currentDayType,
        WeatherType currentWeather,

        // Ocupação.
        long totalSpots,
        long availableSpots,
        long occupiedSpots,
        BigDecimal occupancyRate,

        // Filas.
        int entryQueue,
        int parkingQueue,
        int exitQueue,

        // Sessões atuais.
        long activeSessions,
        long enteringSessions,
        long exitingSessions,

        // Estatísticas acumuladas.
        long completedSessions,

        // Indicadores de desempenho.
        Duration averageStay,
        Duration averageEntryWait,
        Duration averageParkingWait,
        Duration averageExitWait,

        BigDecimal entryFlowRate,
        BigDecimal exitFlowRate,

        List<GateMetrics> entryGates,
        List<GateMetrics> exitGates

) {
}
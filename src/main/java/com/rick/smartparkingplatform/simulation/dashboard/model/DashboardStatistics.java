package com.rick.smartparkingplatform.simulation.dashboard.model;

import com.rick.smartparkingplatform.simulation.metrics.statistics.GateMetrics;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

public record DashboardStatistics(

        Long completedSessions,

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
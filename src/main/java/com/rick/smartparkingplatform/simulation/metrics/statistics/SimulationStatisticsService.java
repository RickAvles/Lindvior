package com.rick.smartparkingplatform.simulation.metrics.statistics;

import com.rick.smartparkingplatform.simulation.engine.SimulationClock;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class SimulationStatisticsService {

    private final SimulationClock simulationClock;

    private LocalDateTime simulationStartedAt;

    // =====================================================
    // PERMANÊNCIA
    // =====================================================

    private Duration totalStayTime = Duration.ZERO;
    private long completedSessions;

    // =====================================================
    // TEMPOS DE ESPERA
    // =====================================================

    private Duration totalEntryWaitTime = Duration.ZERO;
    private long entryWaitCount;

    private Duration totalParkingWaitTime = Duration.ZERO;
    private long parkingWaitCount;

    private Duration totalExitWaitTime = Duration.ZERO;
    private long exitWaitCount;

    // =====================================================
    // FLUXO
    // =====================================================

    private long totalEntries;
    private long totalExits;

    public SimulationStatisticsService(SimulationClock simulationClock) {
        this.simulationClock = simulationClock;
    }

    // =====================================================
    // CICLO DE VIDA
    // =====================================================

    public void start(LocalDateTime currentTime) {

        if (hasStarted()) {
            return;
        }

        simulationStartedAt = currentTime;
    }

    public boolean hasStarted() {

        return simulationStartedAt != null;
    }

    public void reset() {

        simulationStartedAt = null;

        totalStayTime = Duration.ZERO;
        completedSessions = 0;

        totalEntryWaitTime = Duration.ZERO;
        entryWaitCount = 0;

        totalParkingWaitTime = Duration.ZERO;
        parkingWaitCount = 0;

        totalExitWaitTime = Duration.ZERO;
        exitWaitCount = 0;

        totalEntries = 0;
        totalExits = 0;
    }

    // =====================================================
    // REGISTROS
    // =====================================================

    public void recordStay(Duration stay) {

        if (stay.isNegative()) {
            return;
        }

        totalStayTime = totalStayTime.plus(stay);
        completedSessions++;
    }

    public void recordEntryWait(Duration wait) {

        if (wait.isNegative()) {
            return;
        }

        totalEntryWaitTime = totalEntryWaitTime.plus(wait);
        entryWaitCount++;
    }

    public void recordParkingWait(Duration wait) {

        if (wait.isNegative()) {
            return;
        }

        totalParkingWaitTime = totalParkingWaitTime.plus(wait);
        parkingWaitCount++;
    }

    public void recordExitWait(Duration wait) {

        if (wait.isNegative()) {
            return;
        }

        totalExitWaitTime = totalExitWaitTime.plus(wait);
        exitWaitCount++;
    }

    public void recordEntry() {

        totalEntries++;
    }

    public void recordExit() {

        totalExits++;
    }

    // =====================================================
    // MÉDIAS
    // =====================================================

    public Duration getAverageStay() {

        if (completedSessions == 0) {
            return Duration.ZERO;
        }

        return totalStayTime.dividedBy(completedSessions);
    }

    public Duration getAverageEntryWait() {

        if (entryWaitCount == 0) {
            return Duration.ZERO;
        }

        return totalEntryWaitTime.dividedBy(entryWaitCount);
    }

    public Duration getAverageParkingWait() {

        if (parkingWaitCount == 0) {
            return Duration.ZERO;
        }

        return totalParkingWaitTime.dividedBy(parkingWaitCount);
    }

    public Duration getAverageExitWait() {

        if (exitWaitCount == 0) {
            return Duration.ZERO;
        }

        return totalExitWaitTime.dividedBy(exitWaitCount);
    }

    // =====================================================
    // FLUXO (VEÍCULOS/HORA)
    // =====================================================

    public BigDecimal getEntryFlowRate() {

        return calculateFlowRate(totalEntries);
    }

    public BigDecimal getExitFlowRate() {

        return calculateFlowRate(totalExits);
    }

    private BigDecimal calculateFlowRate(long totalVehicles) {

        if (!hasStarted()) {
            return BigDecimal.ZERO;
        }

        long simulatedSeconds = Duration.between(
                simulationStartedAt,
                simulationClock.getCurrentTime()
        ).getSeconds();

        if (simulatedSeconds <= 0) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(totalVehicles)
                .multiply(BigDecimal.valueOf(3600))
                .divide(
                        BigDecimal.valueOf(simulatedSeconds),
                        2,
                        RoundingMode.HALF_UP
                );
    }

}
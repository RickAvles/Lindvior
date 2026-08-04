package com.rick.smartparkingplatform.simulation.parking.exit;

import com.rick.smartparkingplatform.entity.ParkingSession;
import com.rick.smartparkingplatform.service.ParkingSessionService;
import com.rick.smartparkingplatform.service.ParkingSpotService;
import com.rick.smartparkingplatform.simulation.dashboard.event.DashboardEventPublisher;
import com.rick.smartparkingplatform.simulation.engine.SimulationClock;
import com.rick.smartparkingplatform.simulation.gate.ExitMovementManager;
import com.rick.smartparkingplatform.simulation.gate.Gate;
import com.rick.smartparkingplatform.simulation.log.SimulationLogger;
import com.rick.smartparkingplatform.simulation.metrics.session.SessionMetrics;
import com.rick.smartparkingplatform.simulation.metrics.session.SessionMetricsService;
import com.rick.smartparkingplatform.simulation.metrics.statistics.SimulationStatisticsService;
import com.rick.smartparkingplatform.simulation.queue.ExitGateQueueService;
import com.rick.smartparkingplatform.simulation.queue.ExitQueueService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ParkingExitService {

    // Exit
    private final ParkingExitManager parkingExitManager;

    // Gate
    private final ExitQueueService exitQueueService;
    private final ExitGateQueueService exitGateQueueService;
    private final ExitMovementManager exitMovementManager;

    // Domain
    private final ParkingSpotService parkingSpotService;
    private final ParkingSessionService parkingSessionService;

    // Metrics
    private final SimulationStatisticsService simulationStatisticsService;
    private final SessionMetricsService sessionMetricsService;

    // Simulation
    private final SimulationClock simulationClock;
    private final SimulationLogger simulationLogger;
    private final DashboardEventPublisher dashboardEventPublisher;


    // Processa a saída de veículos.
    @Transactional
    public void process() {

        processExit();

        processGateCrossing();
    }

    // Adiciona um veículo à fila de saída.
    public void startExit(ParkingSession parkingSession) {

        SessionMetrics sessionMetrics =
                sessionMetricsService.get(parkingSession);

        sessionMetrics.setExitQueueAt(
                simulationClock.getCurrentTime()
        );

        parkingExitManager.startExit(parkingSession);

        dashboardEventPublisher.publishVehicleLeftSpot(parkingSession);
    }

    // Move um veículo da fila de saída para a cancela.
    private void processExit() {

        if (!exitQueueService.hasWaitingSessions()) {
            return;
        }

        Optional<Gate> availableGate = parkingExitManager.getAvailableGate();

        if (availableGate.isEmpty()) {
            return;
        }

        Gate gate = availableGate.get();

        ParkingSession parkingSession = exitQueueService.dequeue();

        simulationLogger.leave(
                parkingSession.getVehicle().getLicensePlate()
        );

        parkingSession.setExitGate(gate);

        // Inicia o processamento da cancela.
        exitMovementManager.startGateCrossing(
                parkingSession,
                gate,
                simulationClock.getCurrentTime()
        );

        exitGateQueueService.enqueue(parkingSession);

        // Inicia o cooldown da cancela utilizada.
        parkingExitManager.startCooldown(
                gate,
                simulationClock.getCurrentTime()
        );
    }

    // Processa os veículos que terminaram de atravessar a cancela.
    private void processGateCrossing() {

        for (ParkingSession parkingSession : exitGateQueueService.getWaitingSessions()) {

            if (!exitMovementManager.hasFinishedCrossing(
                    parkingSession,
                    simulationClock.getCurrentTime())) {
                continue;
            }

            parkingSpotService.release(
                    parkingSession.getParkingSpot()
            );

            parkingSessionService.closeSession(
                    parkingSession,
                    simulationClock.getCurrentTime()
            );

            dashboardEventPublisher.publishVehicleExited(parkingSession);

            SessionMetrics sessionMetrics =
                    sessionMetricsService.get(parkingSession);

            sessionMetrics.setFinishedAt(
                    simulationClock.getCurrentTime()
            );

            if (sessionMetrics.getExitQueueAt() != null) {

                simulationStatisticsService.recordExitWait(
                        Duration.between(
                                sessionMetrics.getExitQueueAt(),
                                sessionMetrics.getFinishedAt()
                        )
                );
            }

            simulationStatisticsService.recordStay(
                    Duration.between(
                            parkingSession.getEntryTime(),
                            parkingSession.getExitTime()
                    )
            );

            simulationStatisticsService.recordExit();

            sessionMetricsService.remove(parkingSession);

            exitMovementManager.finishGateCrossing(
                    parkingSession,
                    parkingSession.getExitGate()
            );

            exitGateQueueService.remove(parkingSession);
        }
    }

}
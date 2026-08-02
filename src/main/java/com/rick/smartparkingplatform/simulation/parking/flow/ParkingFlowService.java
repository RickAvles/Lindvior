package com.rick.smartparkingplatform.simulation.parking.flow;

import com.rick.smartparkingplatform.entity.ParkingSession;
import com.rick.smartparkingplatform.service.ParkingSessionService;
import com.rick.smartparkingplatform.service.ParkingSpotService;
import com.rick.smartparkingplatform.simulation.dashboard.event.DashboardEventPublisher;
import com.rick.smartparkingplatform.simulation.engine.SimulationClock;
import com.rick.smartparkingplatform.simulation.log.SimulationLogger;
import com.rick.smartparkingplatform.simulation.metrics.session.SessionMetrics;
import com.rick.smartparkingplatform.simulation.metrics.session.SessionMetricsService;
import com.rick.smartparkingplatform.simulation.metrics.statistics.SimulationStatisticsService;
import com.rick.smartparkingplatform.simulation.queue.ParkingQueueService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParkingFlowService {

    // Movement
    private final ParkingMovementManager parkingMovementManager;

    // Queue
    private final ParkingQueueService parkingQueueService;

    // Domain
    private final ParkingSpotService parkingSpotService;
    private final ParkingSessionService parkingSessionService;

    // Metrics
    private final SimulationStatisticsService simulationStatisticsService;
    private final SessionMetricsService sessionMetricsService;

    // Infrastructure
    private final SimulationClock simulationClock;
    private final SimulationLogger simulationLogger;
    private final DashboardEventPublisher dashboardEventPublisher;

    // Processa os veículos em deslocamento até a vaga.
    @Transactional
    public void process() {

        LocalDateTime currentTime = simulationClock.getCurrentTime();

        processParking(currentTime);
    }

    // Processa o estacionamento dos veículos.
    private void processParking(LocalDateTime currentTime) {

        List<ParkingSession> sessions = parkingQueueService.getAll();

        for (ParkingSession parkingSession : sessions) {

            if (!parkingMovementManager.hasFinishedSearching(
                    parkingSession,
                    currentTime
            )) {
                continue;
            }

            parkingSpotService.occupy(parkingSession.getParkingSpot());

            parkingSessionService.park(parkingSession);

            dashboardEventPublisher.publishVehicleParked(parkingSession);

            SessionMetrics sessionMetrics = sessionMetricsService.get(parkingSession);

            sessionMetrics.setParkedAt(currentTime);

            if (sessionMetrics.getEntryGateAt() != null) {

                simulationStatisticsService.recordParkingWait(
                        Duration.between(
                                sessionMetrics.getEntryGateAt(),
                                sessionMetrics.getParkedAt()
                        )
                );
            }

            parkingQueueService.remove(parkingSession);

            parkingMovementManager.finishParkingSearch(parkingSession);

            simulationLogger.entry(
                    parkingSession.getVehicle().getLicensePlate()
            );
        }
    }

}
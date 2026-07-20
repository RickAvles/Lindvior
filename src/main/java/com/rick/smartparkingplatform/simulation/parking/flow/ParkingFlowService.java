package com.rick.smartparkingplatform.simulation.parking.flow;

import com.rick.smartparkingplatform.entity.ParkingSession;
import com.rick.smartparkingplatform.service.ParkingSessionService;
import com.rick.smartparkingplatform.service.ParkingSpotService;
import com.rick.smartparkingplatform.simulation.engine.SimulationClock;
import com.rick.smartparkingplatform.simulation.log.SimulationLogger;
import com.rick.smartparkingplatform.simulation.queue.ParkingQueueService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

    // Infrastructure
    private final SimulationClock simulationClock;
    private final SimulationLogger simulationLogger;

    @Transactional
    public void process() {

        LocalDateTime currentTime = simulationClock.getCurrentTime();

        processParking(currentTime);
    }

    // Processa o deslocamento do veículo até a vaga reservada.
    private void processParking(LocalDateTime currentTime) {

        if (parkingMovementManager.isParkingBlocked(currentTime)) {
            return;
        }

        ParkingSession parkingSession = parkingQueueService.dequeue();

        if (parkingSession == null) {
            return;
        }

        parkingSpotService.occupy(parkingSession.getParkingSpot());

        parkingSessionService.park(parkingSession);

        simulationLogger.entry(parkingSession.getVehicle().getLicensePlate());

        parkingMovementManager.registerParking(currentTime);
    }

}
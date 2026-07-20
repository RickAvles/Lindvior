package com.rick.smartparkingplatform.simulation.parking.exit;

import com.rick.smartparkingplatform.entity.ParkingSession;
import com.rick.smartparkingplatform.simulation.engine.SimulationClock;
import com.rick.smartparkingplatform.simulation.log.SimulationLogger;
import com.rick.smartparkingplatform.simulation.queue.ExitQueueService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ParkingExitService {

    private final ParkingExitManager parkingExitManager;
    private final ExitQueueService exitQueueService;
    private final SimulationClock simulationClock;
    private final SimulationLogger simulationLogger;

    @Transactional
    public void process() {

        processExit();
    }

    // Adiciona um veículo à fila de saída.
    public void startExit(ParkingSession parkingSession) {

        parkingExitManager.startExit(parkingSession);
    }

    // Processa a fila de saída.
    private void processExit() {

        LocalDateTime currentTime = simulationClock.getCurrentTime();

        if (!parkingExitManager.canProcessExit(currentTime)) {
            return;
        }

        ParkingSession parkingSession = exitQueueService.dequeue();

        simulationLogger.leave(parkingSession.getVehicle().getLicensePlate());

        parkingExitManager.processExit(parkingSession, currentTime);
    }

}
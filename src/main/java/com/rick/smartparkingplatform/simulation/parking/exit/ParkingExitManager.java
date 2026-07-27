package com.rick.smartparkingplatform.simulation.parking.exit;

import com.rick.smartparkingplatform.entity.Parking;
import com.rick.smartparkingplatform.entity.ParkingSession;
import com.rick.smartparkingplatform.service.ParkingService;
import com.rick.smartparkingplatform.service.ParkingSessionService;
import com.rick.smartparkingplatform.simulation.engine.SimulationClock;
import com.rick.smartparkingplatform.simulation.gate.ExitGateManager;
import com.rick.smartparkingplatform.simulation.gate.Gate;
import com.rick.smartparkingplatform.simulation.queue.ExitQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class ParkingExitManager {

    private final ParkingSessionService parkingSessionService;
    private final ExitQueueService exitQueueService;
    private final ParkingService parkingService;
    private final ExitGateManager exitGateManager;
    private final SimulationClock simulationClock;

    // Retorna uma cancela disponível para processamento.
    public Optional<Gate> getAvailableGate() {

        return exitGateManager.getAvailableGate(
                simulationClock.getCurrentTime()
        );
    }

    // Adiciona um veículo à fila de saída.
    public void startExit(ParkingSession parkingSession) {

        parkingSessionService.validateActiveSession(parkingSession);

        parkingSessionService.startExit(parkingSession);

        exitQueueService.enqueue(parkingSession);
    }

    // Inicia um novo período de cooldown da cancela.
    public void startCooldown(Gate gate, LocalDateTime currentTime) {

        Parking parking = parkingService.getCurrentParking();

        int cooldown = ThreadLocalRandom.current().nextInt(
                parking.getExitGateMinProcessingSeconds(),
                parking.getExitGateMaxProcessingSeconds() + 1
        );

        exitGateManager.startCooldown(
                gate,
                currentTime,
                cooldown
        );
    }

}
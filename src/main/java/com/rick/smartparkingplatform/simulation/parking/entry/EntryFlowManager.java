package com.rick.smartparkingplatform.simulation.parking.entry;

import com.rick.smartparkingplatform.entity.Parking;
import com.rick.smartparkingplatform.service.ParkingService;
import com.rick.smartparkingplatform.simulation.engine.SimulationClock;
import com.rick.smartparkingplatform.simulation.gate.EntryGateManager;
import com.rick.smartparkingplatform.simulation.gate.Gate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class EntryFlowManager {

    private final SimulationClock simulationClock;
    private final ParkingService parkingService;
    private final EntryGateManager entryGateManager;

    // Retorna uma cancela disponível para processamento.
    public Optional<Gate> getAvailableGate() {

        return entryGateManager.getAvailableGate(
                simulationClock.getCurrentTime()
        );

    }

    // Inicia o processamento da cancela.
    public void startCooldown(Gate gate) {

        Parking parking = parkingService.getCurrentParking();

        int cooldown = ThreadLocalRandom.current().nextInt(
                parking.getEntryGateMinProcessingSeconds(),
                parking.getEntryGateMaxProcessingSeconds() + 1
        );

        entryGateManager.startCooldown(
                gate,
                simulationClock.getCurrentTime(),
                cooldown
        );

    }

}
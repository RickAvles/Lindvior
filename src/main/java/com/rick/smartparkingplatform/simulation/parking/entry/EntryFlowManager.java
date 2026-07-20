package com.rick.smartparkingplatform.simulation.parking.entry;

import com.rick.smartparkingplatform.simulation.engine.SimulationClock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class EntryFlowManager {

    private static final int MIN_COOLDOWN_SECONDS = 10;
    private static final int MAX_COOLDOWN_SECONDS = 20;

    private final SimulationClock simulationClock;

    private LocalDateTime nextEntryAt;

    // Verifica se a cancela pode processar o próximo veículo.
    public boolean canProcessNextVehicle() {

        if (nextEntryAt == null) {
            return true;
        }

        return !simulationClock.getCurrentTime().isBefore(nextEntryAt);
    }

    // Inicia o tempo de espera para a próxima entrada.
    public void startCooldown() {

        int cooldown = ThreadLocalRandom.current().nextInt(MIN_COOLDOWN_SECONDS, MAX_COOLDOWN_SECONDS + 1);

        nextEntryAt = simulationClock.getCurrentTime().plusSeconds(cooldown);
    }

}
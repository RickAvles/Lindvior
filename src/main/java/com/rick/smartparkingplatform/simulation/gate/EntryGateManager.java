package com.rick.smartparkingplatform.simulation.gate;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EntryGateManager {

    // Cancelas de entrada da simulação.
    private final List<Gate> gates = new ArrayList<>();

    // Inicializa as cancelas de entrada.
    public void initialize(int numberOfGates) {

        gates.clear();

        for (int i = 1; i <= numberOfGates; i++) {
            gates.add(new Gate(i));
        }

    }

    // Retorna a primeira cancela disponível.
    public Optional<Gate> getAvailableGate(LocalDateTime currentTime) {

        for (Gate gate : gates) {

            if (!gate.getAvailableAt().isAfter(currentTime)) {
                return Optional.of(gate);
            }

        }

        return Optional.empty();

    }

    // Inicia o processamento da cancela.
    public void startCooldown(Gate gate, LocalDateTime currentTime, int cooldownSeconds) {

        gate.setAvailableAt(currentTime.plusSeconds(cooldownSeconds));

    }

    //Retorna uma cópia das cancelas da simulação.
    public List<Gate> getGates() {

        return List.copyOf(gates);

    }

}
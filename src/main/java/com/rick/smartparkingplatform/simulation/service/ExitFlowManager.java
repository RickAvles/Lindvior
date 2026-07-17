package com.rick.smartparkingplatform.simulation.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ExitFlowManager {

    @Value("${simulation.exit.cooldown-seconds}")
    private long cooldownSeconds;

    private LocalDateTime nextAvailableExitTime = LocalDateTime.MIN;

    /**
     * Verifica se a saída pode processar
     * um novo veículo neste momento.
     */
    public boolean canProcessExit(LocalDateTime currentTime) {

        return !currentTime.isBefore(nextAvailableExitTime);
    }

    /**
     * Registra a utilização da saída,
     * iniciando um novo período de cooldown.
     */
    public void registerExit(LocalDateTime currentTime) {

        nextAvailableExitTime =
                currentTime.plusSeconds(cooldownSeconds);
    }

}
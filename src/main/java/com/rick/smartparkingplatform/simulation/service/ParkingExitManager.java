package com.rick.smartparkingplatform.simulation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParkingExitManager {

    private final ExitQueueService exitQueueService;

    /**
     * Determina se existe algum veículo
     * aguardando para deixar o estacionamento.
     */
    public boolean shouldProcessExit() {

        return exitQueueService.hasVehicles();
    }

}
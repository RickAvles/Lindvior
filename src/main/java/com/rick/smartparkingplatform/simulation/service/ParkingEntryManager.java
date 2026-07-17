package com.rick.smartparkingplatform.simulation.service;

import com.rick.smartparkingplatform.dto.response.OccupancyResponse;
import com.rick.smartparkingplatform.service.ParkingSpotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class ParkingEntryManager {

    private final TrafficProfileService trafficProfileService;
    private final ParkingSpotService parkingSpotService;
    private final Random random = new Random();
    private final EntryQueueService entryQueueService;

    private static final double MAX_QUEUE_SIZE = 50.0;

    /**
     * Determina se um novo veículo deverá
     * chegar ao estacionamento neste ciclo.
     */
    public boolean shouldGenerateEntry() {

        double baseProbability =
                trafficProfileService.getEntryProbability();

        OccupancyResponse occupancy =
                parkingSpotService.getOccupancy();

        if (occupancy.availableSpots() == 0) {
            return false;
        }

        double finalProbability =
                baseProbability *
                        calculateOccupancyFactor(occupancy) *
                        calculateQueueFactor();

        return random.nextDouble() < finalProbability;
    }

    /**
     * Obtém a probabilidade de saída
     * para o perfil informado.
     */
    private double calculateOccupancyFactor(OccupancyResponse occupancy) {

        return 1 - (occupancy.occupancyRate().doubleValue() / 100.0);
    }

    /**
     * Calcula o fator de redução da geração
     * conforme o tamanho atual da fila.
     */
    private double calculateQueueFactor() {

        int queueSize = entryQueueService.size();

        double factor = 1.0 - (queueSize / MAX_QUEUE_SIZE);

        return Math.max(factor, 0.0);
    }

}
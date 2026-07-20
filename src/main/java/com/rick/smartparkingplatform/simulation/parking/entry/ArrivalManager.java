package com.rick.smartparkingplatform.simulation.parking.entry;

import com.rick.smartparkingplatform.service.ParkingSpotService;
import com.rick.smartparkingplatform.simulation.queue.EntryQueueService;
import com.rick.smartparkingplatform.simulation.service.TrafficProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class ArrivalManager {

    private static final double MAX_QUEUE_SIZE = 50.0;

    private final TrafficProfileService trafficProfileService;
    private final ParkingSpotService parkingSpotService;
    private final EntryQueueService entryQueueService;

    // Determina se um novo veículo deve chegar ao estacionamento.
    public boolean shouldGenerateVehicle() {

        if (!parkingSpotService.hasAvailableSpot()) {
            return false;
        }

        double probability = calculateEntryProbability();

        return ThreadLocalRandom.current().nextDouble() < probability;
    }

    // Calcula a probabilidade final de geração de um novo veículo.
    private double calculateEntryProbability() {

        return trafficProfileService.getEntryProbability()
                * calculateOccupancyFactor()
                * calculateQueueFactor();
    }

    // Calcula o fator conforme a ocupação do estacionamento.
    private double calculateOccupancyFactor() {

        return 1 - parkingSpotService.getOccupancyRate();
    }

    // Calcula o fator de redução conforme o tamanho da fila.
    private double calculateQueueFactor() {

        double factor =
                1.0 - ((double) entryQueueService.size() / MAX_QUEUE_SIZE);

        return Math.max(factor, 0.0);
    }

}
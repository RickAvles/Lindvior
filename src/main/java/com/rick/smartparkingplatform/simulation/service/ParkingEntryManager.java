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

    /**
     * Determina se um novo veículo
     * deverá entrar neste ciclo.
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
                baseProbability * calculateOccupancyFactor(
                        occupancy
                );

        return random.nextDouble() < finalProbability;
    }

    /**
     * Obtém a probabilidade de saída
     * para o perfil informado.
     */
    private double calculateOccupancyFactor(
            OccupancyResponse occupancy) {

        return 1 - (occupancy.occupancyRate().doubleValue() / 100.0);
    }

}
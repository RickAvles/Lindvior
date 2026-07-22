package com.rick.smartparkingplatform.simulation.vehicle;

import com.rick.smartparkingplatform.entity.Vehicle;
import com.rick.smartparkingplatform.service.ParkingSessionService;
import com.rick.smartparkingplatform.service.ParkingSpotService;
import com.rick.smartparkingplatform.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class VehicleSelectionManager {

    private static final int MAX_SELECTION_ATTEMPTS = 20;

    private final VehicleService vehicleService;
    private final ParkingSpotService parkingSpotService;
    private final ParkingSessionService parkingSessionService;
    private final GeneratedVehicleFactory generatedVehicleFactory;

    // Retorna um veículo para a próxima entrada da simulação.
    public Vehicle selectNextVehicle() {

        double probability = calculateNewVehicleProbability();

        if (ThreadLocalRandom.current().nextDouble() < probability) {
            return generatedVehicleFactory.createVehicle();
        }

        return getExistingVehicle();
    }

    // Calcula a probabilidade de geração de um novo veículo.
    private double calculateNewVehicleProbability() {

        long population = vehicleService.count();

        long capacity = parkingSpotService.getCapacity();

        long threshold = capacity * 2L;

        if (population < threshold) {
            return 1.0;
        }

        double probability = 0.5;

        long currentThreshold = threshold;

        while (population >= currentThreshold * 2) {

            probability /= 2;
            currentThreshold *= 2;
        }

        return probability;
    }

    // Seleciona um veículo que não esteja estacionado.
    private Vehicle getExistingVehicle() {

        for (int attempt = 0; attempt < MAX_SELECTION_ATTEMPTS; attempt++) {

            int position = ThreadLocalRandom.current().nextInt((int) vehicleService.count());

            Vehicle vehicle = vehicleService.getVehicleAtPosition(position);

            if (!parkingSessionService.existsOpenSession(vehicle.getId())) {
                return vehicle;
            }
        }

        return generatedVehicleFactory.createVehicle();
    }

}
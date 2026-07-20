package com.rick.smartparkingplatform.simulation.vehicle;

import com.rick.smartparkingplatform.entity.Vehicle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VehicleGenerator {

    private final VehicleSelectionManager vehicleSelectionManager;

    // Retorna o próximo veículo da simulação.
    public Vehicle generateVehicle() {
        return vehicleSelectionManager.selectNextVehicle();
    }

}
package com.rick.smartparkingplatform.simulation.vehicle;

import com.rick.smartparkingplatform.entity.Vehicle;
import com.rick.smartparkingplatform.simulation.parking.entry.VehicleProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VehicleProviderImpl implements VehicleProvider {

    private final VehicleGenerator vehicleGenerator;

    @Override
    public Vehicle nextVehicle() {
        return vehicleGenerator.generateVehicle();
    }

}
package com.rick.smartparkingplatform.simulation.vehicle;

import com.rick.smartparkingplatform.entity.Vehicle;
import com.rick.smartparkingplatform.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeneratedVehicleFactory {

    private final VehicleService vehicleService;

    private final LicensePlateGenerator licensePlateGenerator;
    private final VehicleAttributeGenerator vehicleAttributeGenerator;

    // Cria e cadastra um novo veículo gerado pela simulação.
    public Vehicle createVehicle() {

        String licensePlate;

        do {
            licensePlate = licensePlateGenerator.generateLicensePlate();
        } while (vehicleService.existsByLicensePlate(licensePlate));

        return vehicleService.createGeneratedVehicle(
                licensePlate,
                vehicleAttributeGenerator.generateVehicleType(),
                vehicleAttributeGenerator.generateColor(),
                vehicleAttributeGenerator.generateStayProfile()
        );
    }

}
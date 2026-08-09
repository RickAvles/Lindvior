package com.rick.smartparkingplatform.domain.simulation;

import com.rick.smartparkingplatform.entity.Vehicle;
import com.rick.smartparkingplatform.simulation.vehicle.VehicleGenerator;
import com.rick.smartparkingplatform.simulation.vehicle.VehicleSelectionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehicleGeneratorTest {

    @Mock
    private VehicleSelectionManager vehicleSelectionManager;

    @InjectMocks
    private VehicleGenerator vehicleGenerator;

    private Vehicle vehicle;

    @BeforeEach
    void setUp() {

        vehicle = new Vehicle();
    }

    // Retorna o veículo fornecido pelo gerenciador de seleção.
    @Test
    void shouldGenerateVehicleFromSelectionManager() {

        when(vehicleSelectionManager.selectNextVehicle())
                .thenReturn(vehicle);

        Vehicle result =
                vehicleGenerator.generateVehicle();

        assertSame(
                vehicle,
                result
        );

        verify(vehicleSelectionManager)
                .selectNextVehicle();
    }
}
package com.rick.smartparkingplatform.domain.simulation;

import com.rick.smartparkingplatform.entity.Vehicle;
import com.rick.smartparkingplatform.service.ParkingSessionService;
import com.rick.smartparkingplatform.service.ParkingSpotService;
import com.rick.smartparkingplatform.service.VehicleService;
import com.rick.smartparkingplatform.simulation.vehicle.GeneratedVehicleFactory;
import com.rick.smartparkingplatform.simulation.vehicle.VehicleSelectionManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehicleSelectionManagerTest {

    @Mock
    private VehicleService vehicleService;

    @Mock
    private ParkingSpotService parkingSpotService;

    @Mock
    private ParkingSessionService parkingSessionService;

    @Mock
    private GeneratedVehicleFactory generatedVehicleFactory;

    @InjectMocks
    private VehicleSelectionManager vehicleSelectionManager;

    // Quando a população está abaixo do limite, a probabilidade de criar
    // um novo veículo é 100%.
    @Test
    void shouldGenerateNewVehicleWhenPopulationIsBelowThreshold() {

        Vehicle vehicle = new Vehicle();

        when(vehicleService.count())
                .thenReturn(5L);

        when(parkingSpotService.getCapacity())
                .thenReturn(10L);

        when(generatedVehicleFactory.createVehicle())
                .thenReturn(vehicle);

        Vehicle result =
                vehicleSelectionManager.selectNextVehicle();

        assertSame(
                vehicle,
                result
        );

        verify(generatedVehicleFactory)
                .createVehicle();
    }

    // Verifica que o limite de população considera duas vezes a capacidade
    // antes de começar a permitir reutilização de veículos.
    @Test
    void shouldGenerateNewVehicleWhenPopulationIsJustBelowDoubleCapacity() {

        Vehicle vehicle = new Vehicle();

        when(vehicleService.count())
                .thenReturn(19L);

        when(parkingSpotService.getCapacity())
                .thenReturn(10L);

        when(generatedVehicleFactory.createVehicle())
                .thenReturn(vehicle);

        Vehicle result =
                vehicleSelectionManager.selectNextVehicle();

        assertSame(
                vehicle,
                result
        );

        verify(generatedVehicleFactory)
                .createVehicle();
    }
}

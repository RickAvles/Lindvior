package com.rick.smartparkingplatform.domain.simulation;

import com.rick.smartparkingplatform.entity.Vehicle;
import com.rick.smartparkingplatform.enums.VehicleType;
import com.rick.smartparkingplatform.service.VehicleService;
import com.rick.smartparkingplatform.simulation.parking.stay.StayProfile;
import com.rick.smartparkingplatform.simulation.vehicle.GeneratedVehicleFactory;
import com.rick.smartparkingplatform.simulation.vehicle.LicensePlateGenerator;
import com.rick.smartparkingplatform.simulation.vehicle.VehicleAttributeGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeneratedVehicleFactoryTest {

    @Mock
    private VehicleService vehicleService;

    @Mock
    private LicensePlateGenerator licensePlateGenerator;

    @Mock
    private VehicleAttributeGenerator vehicleAttributeGenerator;

    @InjectMocks
    private GeneratedVehicleFactory generatedVehicleFactory;

    // Verifica se o factory cria um veículo PCD quando o atributo gerado é true.
    @Test
    void shouldCreatePcdVehicleWhenGeneratedAttributeIsTrue() {

        String licensePlate = "ABC1D23";
        Vehicle vehicle = new Vehicle();

        when(licensePlateGenerator.generateLicensePlate())
                .thenReturn(licensePlate);

        when(vehicleService.existsByLicensePlate(licensePlate))
                .thenReturn(false);

        when(vehicleAttributeGenerator.generateVehicleType())
                .thenReturn(VehicleType.SEDAN);

        when(vehicleAttributeGenerator.generateColor())
                .thenReturn("White");

        when(vehicleAttributeGenerator.generateStayProfile())
                .thenReturn(StayProfile.NORMAL);

        when(vehicleAttributeGenerator.generatePcd())
                .thenReturn(true);

        when(vehicleService.createGeneratedVehicle(
                licensePlate,
                VehicleType.SEDAN,
                "White",
                StayProfile.NORMAL,
                true
        )).thenReturn(vehicle);

        Vehicle result =
                generatedVehicleFactory.createVehicle();

        assertSame(
                vehicle,
                result
        );

        verify(vehicleService).createGeneratedVehicle(
                eq(licensePlate),
                eq(VehicleType.SEDAN),
                eq("White"),
                eq(StayProfile.NORMAL),
                eq(true)
        );
    }

    // Verifica se o factory preserva o valor false quando o veículo não é PCD.
    @Test
    void shouldCreateNonPcdVehicleWhenGeneratedAttributeIsFalse() {

        String licensePlate = "ABC1D23";
        Vehicle vehicle = new Vehicle();

        when(licensePlateGenerator.generateLicensePlate())
                .thenReturn(licensePlate);

        when(vehicleService.existsByLicensePlate(licensePlate))
                .thenReturn(false);

        when(vehicleAttributeGenerator.generateVehicleType())
                .thenReturn(VehicleType.SEDAN);

        when(vehicleAttributeGenerator.generateColor())
                .thenReturn("Black");

        when(vehicleAttributeGenerator.generateStayProfile())
                .thenReturn(StayProfile.NORMAL);

        when(vehicleAttributeGenerator.generatePcd())
                .thenReturn(false);

        when(vehicleService.createGeneratedVehicle(
                licensePlate,
                VehicleType.SEDAN,
                "Black",
                StayProfile.NORMAL,
                false
        )).thenReturn(vehicle);

        Vehicle result =
                generatedVehicleFactory.createVehicle();

        assertSame(
                vehicle,
                result
        );

        verify(vehicleService).createGeneratedVehicle(
                eq(licensePlate),
                eq(VehicleType.SEDAN),
                eq("Black"),
                eq(StayProfile.NORMAL),
                eq(false)
        );
    }

    // Verifica se uma placa já existente faz o factory gerar outra placa antes de criar o veículo.
    @Test
    void shouldGenerateAnotherLicensePlateWhenFirstOneAlreadyExists() {

        String existingLicensePlate = "ABC1D23";
        String availableLicensePlate = "XYZ9K87";

        Vehicle vehicle = new Vehicle();

        when(licensePlateGenerator.generateLicensePlate())
                .thenReturn(
                        existingLicensePlate,
                        availableLicensePlate
                );

        when(vehicleService.existsByLicensePlate(existingLicensePlate))
                .thenReturn(true);

        when(vehicleService.existsByLicensePlate(availableLicensePlate))
                .thenReturn(false);

        when(vehicleAttributeGenerator.generateVehicleType())
                .thenReturn(VehicleType.SEDAN);

        when(vehicleAttributeGenerator.generateColor())
                .thenReturn("White");

        when(vehicleAttributeGenerator.generateStayProfile())
                .thenReturn(StayProfile.NORMAL);

        when(vehicleAttributeGenerator.generatePcd())
                .thenReturn(true);

        when(vehicleService.createGeneratedVehicle(
                availableLicensePlate,
                VehicleType.SEDAN,
                "White",
                StayProfile.NORMAL,
                true
        )).thenReturn(vehicle);

        Vehicle result =
                generatedVehicleFactory.createVehicle();

        assertSame(
                vehicle,
                result
        );

        verify(vehicleService).createGeneratedVehicle(
                eq(availableLicensePlate),
                eq(VehicleType.SEDAN),
                eq("White"),
                eq(StayProfile.NORMAL),
                eq(true)
        );
    }
}
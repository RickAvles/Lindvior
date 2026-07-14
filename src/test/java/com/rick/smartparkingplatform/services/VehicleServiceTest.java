package com.rick.smartparkingplatform.services;

import com.rick.smartparkingplatform.dto.request.VehicleRequest;
import com.rick.smartparkingplatform.dto.response.VehicleResponse;
import com.rick.smartparkingplatform.entity.Vehicle;
import com.rick.smartparkingplatform.enums.VehicleType;
import com.rick.smartparkingplatform.exception.VehicleAlreadyExistsException;
import com.rick.smartparkingplatform.exception.VehicleNotFoundException;
import com.rick.smartparkingplatform.repository.VehicleRepository;
import com.rick.smartparkingplatform.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleService vehicleService;

    private Vehicle vehicle;
    private VehicleRequest request;

    @BeforeEach
    void setUp() {

        vehicle = new Vehicle();
        vehicle.setId(UUID.randomUUID());
        vehicle.setLicensePlate("ABC1D23");
        vehicle.setType(VehicleType.SEDAN);
        vehicle.setColor("Black");
        vehicle.setActive(true);
        vehicle.setCreatedAt(LocalDateTime.now());

        request = new VehicleRequest(
                "XYZ9A87",
                VehicleType.SUV,
                "White"
        );
    }

    @Test
    void shouldCreateVehicle() {

        when(vehicleRepository.existsByLicensePlate(request.licensePlate()))
                .thenReturn(false);

        when(vehicleRepository.save(any(Vehicle.class)))
                .thenAnswer(invocation -> {
                    Vehicle saved = invocation.getArgument(0);
                    saved.setId(UUID.randomUUID());
                    return saved;
                });

        VehicleResponse response = vehicleService.create(request);

        assertNotNull(response);
        assertEquals(request.licensePlate(), response.licensePlate());

        ArgumentCaptor<Vehicle> captor =
                ArgumentCaptor.forClass(Vehicle.class);

        verify(vehicleRepository).save(captor.capture());

        Vehicle saved = captor.getValue();

        assertEquals(request.licensePlate(), saved.getLicensePlate());
        assertEquals(request.type(), saved.getType());
        assertEquals(request.color(), saved.getColor());
        assertTrue(saved.isActive());
    }

    @Test
    void shouldThrowExceptionWhenVehicleAlreadyExists() {

        when(vehicleRepository.existsByLicensePlate(request.licensePlate()))
                .thenReturn(true);

        assertThrows(
                VehicleAlreadyExistsException.class,
                () -> vehicleService.create(request)
        );

        verify(vehicleRepository, never()).save(any());
    }

    @Test
    void shouldReturnVehicleById() {

        when(vehicleRepository.findById(vehicle.getId()))
                .thenReturn(Optional.of(vehicle));

        VehicleResponse response =
                vehicleService.getById(vehicle.getId());

        assertEquals(vehicle.getId(), response.id());
        assertEquals(vehicle.getLicensePlate(), response.licensePlate());
    }

    @Test
    void shouldThrowExceptionWhenVehicleDoesNotExist() {

        UUID id = UUID.randomUUID();

        when(vehicleRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                VehicleNotFoundException.class,
                () -> vehicleService.getById(id)
        );
    }

    @Test
    void shouldReturnAllVehicles() {

        Page<Vehicle> page =
                new PageImpl<>(List.of(vehicle));

        when(vehicleRepository.findAll(PageRequest.of(0, 10)))
                .thenReturn(page);

        Page<VehicleResponse> response =
                vehicleService.findAll(PageRequest.of(0, 10));

        assertEquals(1, response.getTotalElements());
        assertEquals(vehicle.getLicensePlate(),
                response.getContent().getFirst().licensePlate());
    }

    @Test
    void shouldUpdateVehicle() {

        when(vehicleRepository.findById(vehicle.getId()))
                .thenReturn(Optional.of(vehicle));

        when(vehicleRepository.save(any(Vehicle.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        VehicleResponse response =
                vehicleService.update(vehicle.getId(), request);

        assertEquals(request.licensePlate(), response.licensePlate());
        assertEquals(request.type(), response.type());
        assertEquals(request.color(), response.color());
    }

    @Test
    void shouldDeactivateVehicle() {

        when(vehicleRepository.findById(vehicle.getId()))
                .thenReturn(Optional.of(vehicle));

        when(vehicleRepository.save(any(Vehicle.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        VehicleResponse response =
                vehicleService.deactivate(vehicle.getId());

        assertFalse(response.active());
    }

}
package com.rick.smartparkingplatform.services;

import com.rick.smartparkingplatform.dto.request.ParkingRequest;
import com.rick.smartparkingplatform.dto.response.ParkingResponse;
import com.rick.smartparkingplatform.entity.Parking;
import com.rick.smartparkingplatform.exception.ParkingNotFoundException;
import com.rick.smartparkingplatform.exception.ResourceNotFoundException;
import com.rick.smartparkingplatform.repository.ParkingRepository;
import com.rick.smartparkingplatform.service.ParkingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingServiceTest {

    @Mock
    private ParkingRepository parkingRepository;

    @InjectMocks
    private ParkingService parkingService;

    private Parking parking;
    private ParkingRequest request;

    @BeforeEach
    void setUp() {

        parking = new Parking();
        parking.setId(UUID.randomUUID());
        parking.setName("Shopping Salvador");
        parking.setAddress("Salvador");
        parking.setCapacity(500);
        parking.setActive(true);
        parking.setCreatedAt(LocalDateTime.now());

        request = new ParkingRequest(
                "Shopping Bahia",
                "Lauro de Freitas",
                700,
                false
        );
    }

    @Test
    void shouldReturnParking() {

        when(parkingRepository.findFirstByOrderByCreatedAtAsc())
                .thenReturn(Optional.of(parking));

        ParkingResponse response = parkingService.getParking();

        assertNotNull(response);
        assertEquals(parking.getId(), response.id());
        assertEquals(parking.getName(), response.name());
        assertEquals(parking.getAddress(), response.address());
        assertEquals(parking.getCapacity(), response.capacity());
        assertEquals(parking.isActive(), response.active());
    }

    @Test
    void shouldThrowExceptionWhenParkingDoesNotExist() {

        when(parkingRepository.findFirstByOrderByCreatedAtAsc())
                .thenReturn(Optional.empty());

        assertThrows(
                ParkingNotFoundException.class,
                () -> parkingService.getParking()
        );
    }

    @Test
    void shouldUpdateParking() {

        when(parkingRepository.findById(parking.getId()))
                .thenReturn(Optional.of(parking));

        when(parkingRepository.save(any(Parking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ParkingResponse response =
                parkingService.update(parking.getId(), request);

        assertEquals(request.name(), response.name());
        assertEquals(request.address(), response.address());
        assertEquals(request.capacity(), response.capacity());
        assertEquals(request.active(), response.active());

        ArgumentCaptor<Parking> captor =
                ArgumentCaptor.forClass(Parking.class);

        verify(parkingRepository).save(captor.capture());

        Parking updatedParking = captor.getValue();

        assertEquals(request.name(), updatedParking.getName());
        assertEquals(request.address(), updatedParking.getAddress());
        assertEquals(request.capacity(), updatedParking.getCapacity());
        assertEquals(request.active(), updatedParking.isActive());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingParking() {

        UUID id = UUID.randomUUID();

        when(parkingRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> parkingService.update(id, request)
        );

        verify(parkingRepository, never()).save(any());
    }

}
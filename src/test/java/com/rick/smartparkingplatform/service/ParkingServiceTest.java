package com.rick.smartparkingplatform.service;

import com.rick.smartparkingplatform.dto.request.ParkingRequest;
import com.rick.smartparkingplatform.dto.response.ParkingResponse;
import com.rick.smartparkingplatform.entity.Parking;
import com.rick.smartparkingplatform.exception.ParkingNotFoundException;
import com.rick.smartparkingplatform.exception.ResourceNotFoundException;
import com.rick.smartparkingplatform.repository.ParkingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @Test
    void getParkingShouldThrowParkingNotFoundException() {

        when(parkingRepository.findFirstByOrderByCreatedAtAsc())
                .thenReturn(Optional.empty());

        assertThrows(
                ParkingNotFoundException.class,
                () -> parkingService.getParking()
        );

        verify(parkingRepository)
                .findFirstByOrderByCreatedAtAsc();
    }

    @Test
    void getParkingShouldReturnParking() {

        Parking parking = new Parking();
        parking.setId(UUID.randomUUID());
        parking.setName("Shopping Salvador");
        parking.setAddress("Av. Tancredo Neves");
        parking.setCreatedAt(LocalDateTime.now());

        when(parkingRepository.findFirstByOrderByCreatedAtAsc())
                .thenReturn(Optional.of(parking));

        ParkingResponse response = parkingService.getParking();

        assertNotNull(response);
        assertEquals(parking.getId(), response.id());
        assertEquals("Shopping Salvador", response.name());
        assertEquals("Av. Tancredo Neves", response.address());

        verify(parkingRepository)
                .findFirstByOrderByCreatedAtAsc();
    }

    @Test
    void getCurrentParkingEntityShouldThrowParkingNotFoundException() {

        when(parkingRepository.findFirstByOrderByCreatedAtAsc())
                .thenReturn(Optional.empty());

        assertThrows(
                ParkingNotFoundException.class,
                () -> parkingService.getCurrentParkingEntity()
        );

        verify(parkingRepository)
                .findFirstByOrderByCreatedAtAsc();
    }

    @Test
    void getCurrentParkingEntityShouldReturnParking() {

        Parking parking = new Parking();
        parking.setId(UUID.randomUUID());
        parking.setName("Shopping Salvador");
        parking.setAddress("Av. Tancredo Neves");
        parking.setCreatedAt(LocalDateTime.now());

        when(parkingRepository.findFirstByOrderByCreatedAtAsc())
                .thenReturn(Optional.of(parking));

        Parking response = parkingService.getCurrentParkingEntity();

        assertNotNull(response);
        assertEquals(parking.getId(), response.getId());
        assertEquals("Shopping Salvador", response.getName());
        assertEquals("Av. Tancredo Neves", response.getAddress());

        verify(parkingRepository)
                .findFirstByOrderByCreatedAtAsc();
    }

    @Test
    void updateShouldReturnUpdatedParking() {

        UUID id = UUID.randomUUID();

        ParkingRequest request = new ParkingRequest(
                "Novo Nome",
                "Nova Rua"
        );

        Parking parking = new Parking();
        parking.setId(id);
        parking.setName("Antigo Nome");
        parking.setAddress("Antiga Rua");
        parking.setCreatedAt(LocalDateTime.now());

        when(parkingRepository.findById(id))
                .thenReturn(Optional.of(parking));

        when(parkingRepository.save(any(Parking.class)))
                .thenReturn(parking);

        ParkingResponse response = parkingService.update(id, request);

        assertNotNull(response);
        assertEquals("Novo Nome", response.name());
        assertEquals("Nova Rua", response.address());

        assertEquals("Novo Nome", parking.getName());
        assertEquals("Nova Rua", parking.getAddress());

        verify(parkingRepository)
                .findById(id);

        verify(parkingRepository)
                .save(any(Parking.class));
    }

    @Test
    void updateShouldThrowResourceNotFoundException() {

        UUID id = UUID.randomUUID();

        ParkingRequest request = new ParkingRequest(
                "Novo Nome",
                "Nova Rua"
        );

        when(parkingRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> parkingService.update(id, request)
        );

        verify(parkingRepository)
                .findById(id);

        verify(parkingRepository, never())
                .save(any(Parking.class));
    }
}
package com.rick.smartparkingplatform.service;

import com.rick.smartparkingplatform.dto.filter.ParkingSpotFilter;
import com.rick.smartparkingplatform.dto.request.ParkingSpotRequest;
import com.rick.smartparkingplatform.dto.response.ParkingSpotResponse;
import com.rick.smartparkingplatform.entity.Parking;
import com.rick.smartparkingplatform.entity.ParkingSpot;
import com.rick.smartparkingplatform.enums.StatusParkingSpot;
import com.rick.smartparkingplatform.exception.ParkingSpotAlreadyExistsException;
import com.rick.smartparkingplatform.repository.ParkingSpotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingSpotServiceTest {

    @Mock
    private ParkingService parkingService;

    @Mock
    private ParkingSpotRepository parkingSpotRepository;

    @InjectMocks
    private ParkingSpotService parkingSpotService;

    @Test
    void findAllShouldReturnParkingSpotPage() {

        Pageable pageable = PageRequest.of(0, 20);

        ParkingSpotFilter filter = new ParkingSpotFilter(
                null,
                null,
                null,
                null
        );

        ParkingSpot parkingSpot = new ParkingSpot();
        parkingSpot.setId(UUID.randomUUID());
        parkingSpot.setCode("A-01");
        parkingSpot.setSector("A");
        parkingSpot.setFloor(1);
        parkingSpot.setStatus(StatusParkingSpot.FREE);
        parkingSpot.setActive(true);
        parkingSpot.setCreatedAt(LocalDateTime.now());

        Page<ParkingSpot> page = new PageImpl<>(
                List.of(parkingSpot),
                pageable,
                1
        );

        when(parkingSpotRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(page);

        Page<ParkingSpotResponse> response =
                parkingSpotService.findAll(pageable, filter);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());

        ParkingSpotResponse spot = response.getContent().getFirst();

        assertEquals("A-01", spot.code());
        assertEquals("A", spot.sector());
        assertEquals(1, spot.floor());
        assertEquals(StatusParkingSpot.FREE, spot.status());

        verify(parkingSpotRepository)
                .findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void createShouldThrowParkingSpotAlreadyExistsException() {

        Parking parking = new Parking();

        ParkingSpotRequest request = new ParkingSpotRequest(
                "A-01",
                "A",
                1
        );

        when(parkingService.getCurrentParkingEntity())
                .thenReturn(parking);

        when(parkingSpotRepository.existsByCodeAndParking(
                request.code(),
                parking
        )).thenReturn(true);

        assertThrows(
                ParkingSpotAlreadyExistsException.class,
                () -> parkingSpotService.create(request)
        );

        verify(parkingService)
                .getCurrentParkingEntity();

        verify(parkingSpotRepository)
                .existsByCodeAndParking(request.code(), parking);

        verify(parkingSpotRepository, never())
                .save(any(ParkingSpot.class));
    }

    @Test
    void createShouldReturnParkingSpot() {

        Parking parking = new Parking();

        ParkingSpotRequest request = new ParkingSpotRequest(
                "A-01",
                "A",
                1
        );

        when(parkingService.getCurrentParkingEntity())
                .thenReturn(parking);

        when(parkingSpotRepository.existsByCodeAndParking(
                request.code(),
                parking
        )).thenReturn(false);

        ParkingSpot parkingSpot = new ParkingSpot();
        parkingSpot.setId(UUID.randomUUID());
        parkingSpot.setCode("A-01");
        parkingSpot.setSector("A");
        parkingSpot.setFloor(1);
        parkingSpot.setStatus(StatusParkingSpot.FREE);
        parkingSpot.setActive(true);
        parkingSpot.setCreatedAt(LocalDateTime.now());
        parkingSpot.setParking(parking);

        when(parkingSpotRepository.save(any(ParkingSpot.class)))
                .thenReturn(parkingSpot);

        ParkingSpotResponse response =
                parkingSpotService.create(request);

        assertNotNull(response);
        assertEquals("A-01", response.code());
        assertEquals("A", response.sector());
        assertEquals(1, response.floor());
        assertEquals(StatusParkingSpot.FREE, response.status());

        verify(parkingService)
                .getCurrentParkingEntity();

        verify(parkingSpotRepository)
                .existsByCodeAndParking(request.code(), parking);

        verify(parkingSpotRepository)
                .save(any(ParkingSpot.class));
    }
}
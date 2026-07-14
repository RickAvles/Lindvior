package com.rick.smartparkingplatform.services;

import com.rick.smartparkingplatform.dto.filter.ParkingSpotFilter;
import com.rick.smartparkingplatform.dto.request.ParkingSpotRequest;
import com.rick.smartparkingplatform.dto.response.OccupancyResponse;
import com.rick.smartparkingplatform.dto.response.ParkingSpotResponse;
import com.rick.smartparkingplatform.entity.ParkingSector;
import com.rick.smartparkingplatform.entity.ParkingSpot;
import com.rick.smartparkingplatform.enums.SectorType;
import com.rick.smartparkingplatform.enums.StatusParkingSpot;
import com.rick.smartparkingplatform.exception.ParkingSpotAlreadyExistsException;
import com.rick.smartparkingplatform.exception.ResourceNotFoundException;
import com.rick.smartparkingplatform.repository.ParkingSectorRepository;
import com.rick.smartparkingplatform.repository.ParkingSpotRepository;
import com.rick.smartparkingplatform.service.ParkingSpotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingSpotServiceTest {

    @Mock
    private ParkingSpotRepository parkingSpotRepository;

    @Mock
    private ParkingSectorRepository parkingSectorRepository;

    @InjectMocks
    private ParkingSpotService parkingSpotService;

    private ParkingSector parkingSector;
    private ParkingSpot parkingSpot;
    private ParkingSpotRequest request;

    @BeforeEach
    void setUp() {

        parkingSector = new ParkingSector();
        parkingSector.setId(UUID.randomUUID());
        parkingSector.setName("A");
        parkingSector.setType(SectorType.REGULAR);
        parkingSector.setFloor(1);
        parkingSector.setActive(true);
        parkingSector.setCreatedAt(LocalDateTime.now());

        parkingSpot = new ParkingSpot();
        parkingSpot.setId(UUID.randomUUID());
        parkingSpot.setCode("A-001");
        parkingSpot.setStatus(StatusParkingSpot.FREE);
        parkingSpot.setActive(true);
        parkingSpot.setCreatedAt(LocalDateTime.now());
        parkingSpot.setParkingSector(parkingSector);

        request = new ParkingSpotRequest(
                "A-001",
                parkingSector.getId()
        );
    }

    @Test
    void shouldCreateParkingSpot() {

        when(parkingSectorRepository.findById(parkingSector.getId()))
                .thenReturn(Optional.of(parkingSector));

        when(parkingSpotRepository.existsByCodeAndParkingSector(
                request.code(),
                parkingSector))
                .thenReturn(false);

        when(parkingSpotRepository.save(any(ParkingSpot.class)))
                .thenAnswer(invocation -> {
                    ParkingSpot saved = invocation.getArgument(0);
                    saved.setId(UUID.randomUUID());
                    return saved;
                });

        ParkingSpotResponse response =
                parkingSpotService.create(request);

        assertNotNull(response);
        assertEquals(request.code(), response.code());

        ArgumentCaptor<ParkingSpot> captor =
                ArgumentCaptor.forClass(ParkingSpot.class);

        verify(parkingSpotRepository).save(captor.capture());

        ParkingSpot saved = captor.getValue();

        assertEquals(request.code(), saved.getCode());
        assertEquals(StatusParkingSpot.FREE, saved.getStatus());
        assertTrue(saved.isActive());
        assertEquals(parkingSector, saved.getParkingSector());
    }

    @Test
    void shouldThrowExceptionWhenParkingSectorDoesNotExist() {

        when(parkingSectorRepository.findById(parkingSector.getId()))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> parkingSpotService.create(request)
        );

        verify(parkingSpotRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenParkingSpotAlreadyExists() {

        when(parkingSectorRepository.findById(parkingSector.getId()))
                .thenReturn(Optional.of(parkingSector));

        when(parkingSpotRepository.existsByCodeAndParkingSector(
                request.code(),
                parkingSector))
                .thenReturn(true);

        assertThrows(
                ParkingSpotAlreadyExistsException.class,
                () -> parkingSpotService.create(request)
        );

        verify(parkingSpotRepository, never()).save(any());
    }

    @Test
    void shouldReturnAllParkingSpots() {

        Page<ParkingSpot> page =
                new PageImpl<>(List.of(parkingSpot));

        when(parkingSpotRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);

        ParkingSpotFilter filter =
                new ParkingSpotFilter(
                        null,
                        null,
                        null,
                        null,
                        null
                );

        Page<ParkingSpotResponse> response =
                parkingSpotService.findAll(PageRequest.of(0, 10), filter);

        assertEquals(1, response.getTotalElements());
        assertEquals("A-001", response.getContent().getFirst().code());
    }

    @Test
    void shouldReturnZeroOccupancyWhenThereAreNoParkingSpots() {

        when(parkingSpotRepository.countByActiveTrue())
                .thenReturn(0L);

        OccupancyResponse response =
                parkingSpotService.getOccupancy();

        assertEquals(0L, response.totalSpots());
        assertEquals(0L, response.availableSpots());
        assertEquals(0L, response.occupiedSpots());
        assertEquals(BigDecimal.ZERO, response.occupancyRate());
    }

    @Test
    void shouldCalculateOccupancy() {

        when(parkingSpotRepository.countByActiveTrue())
                .thenReturn(10L);

        when(parkingSpotRepository.countByStatusAndActiveTrue(StatusParkingSpot.FREE))
                .thenReturn(7L);

        when(parkingSpotRepository.countByStatusAndActiveTrue(StatusParkingSpot.OCCUPIED))
                .thenReturn(3L);

        OccupancyResponse response =
                parkingSpotService.getOccupancy();

        assertEquals(10L, response.totalSpots());
        assertEquals(7L, response.availableSpots());
        assertEquals(3L, response.occupiedSpots());
        assertEquals(BigDecimal.valueOf(30.00).setScale(2), response.occupancyRate());
    }

}
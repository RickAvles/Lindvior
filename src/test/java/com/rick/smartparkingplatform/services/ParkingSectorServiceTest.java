package com.rick.smartparkingplatform.services;

import com.rick.smartparkingplatform.dto.request.ParkingSectorRequest;
import com.rick.smartparkingplatform.dto.response.ParkingSectorResponse;
import com.rick.smartparkingplatform.entity.Parking;
import com.rick.smartparkingplatform.entity.ParkingSector;
import com.rick.smartparkingplatform.enums.SectorType;
import com.rick.smartparkingplatform.exception.ParkingNotFoundException;
import com.rick.smartparkingplatform.exception.ParkingSectorAlreadyExistsException;
import com.rick.smartparkingplatform.exception.ResourceNotFoundException;
import com.rick.smartparkingplatform.repository.ParkingRepository;
import com.rick.smartparkingplatform.repository.ParkingSectorRepository;
import com.rick.smartparkingplatform.service.ParkingSectorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingSectorServiceTest {

    @Mock
    private ParkingSectorRepository parkingSectorRepository;

    @Mock
    private ParkingRepository parkingRepository;

    @InjectMocks
    private ParkingSectorService parkingSectorService;

    private Parking parking;
    private ParkingSector parkingSector;
    private ParkingSectorRequest request;

    @BeforeEach
    void setUp() {

        parking = new Parking();
        parking.setId(UUID.randomUUID());
        parking.setName("Shopping");
        parking.setAddress("Salvador");
        parking.setCapacity(500);
        parking.setActive(true);
        parking.setCreatedAt(LocalDateTime.now());

        parkingSector = new ParkingSector();
        parkingSector.setId(UUID.randomUUID());
        parkingSector.setName("A");
        parkingSector.setType(SectorType.REGULAR);
        parkingSector.setFloor(1);
        parkingSector.setActive(true);
        parkingSector.setCreatedAt(LocalDateTime.now());
        parkingSector.setParking(parking);

        request = new ParkingSectorRequest(
                "A",
                SectorType.REGULAR,
                1
        );
    }

    @Test
    void shouldCreateParkingSector() {

        when(parkingRepository.findFirstByOrderByCreatedAtAsc())
                .thenReturn(Optional.of(parking));

        when(parkingSectorRepository.existsByNameAndParking("A", parking))
                .thenReturn(false);

        when(parkingSectorRepository.save(any(ParkingSector.class)))
                .thenAnswer(invocation -> {
                    ParkingSector saved = invocation.getArgument(0);
                    saved.setId(UUID.randomUUID());
                    return saved;
                });

        ParkingSectorResponse response = parkingSectorService.create(request);

        assertNotNull(response);
        assertEquals("A", response.name());
        assertEquals(SectorType.REGULAR, response.type());

        ArgumentCaptor<ParkingSector> captor = ArgumentCaptor.forClass(ParkingSector.class);

        verify(parkingSectorRepository).save(captor.capture());

        ParkingSector saved = captor.getValue();

        assertEquals("A", saved.getName());
        assertEquals(SectorType.REGULAR, saved.getType());
        assertEquals(1, saved.getFloor());
        assertTrue(saved.isActive());
        assertEquals(parking, saved.getParking());
    }

    @Test
    void shouldThrowExceptionWhenParkingDoesNotExist() {

        when(parkingRepository.findFirstByOrderByCreatedAtAsc())
                .thenReturn(Optional.empty());

        assertThrows(
                ParkingNotFoundException.class,
                () -> parkingSectorService.create(request)
        );

        verify(parkingSectorRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenSectorAlreadyExists() {

        when(parkingRepository.findFirstByOrderByCreatedAtAsc())
                .thenReturn(Optional.of(parking));

        when(parkingSectorRepository.existsByNameAndParking("A", parking))
                .thenReturn(true);

        assertThrows(
                ParkingSectorAlreadyExistsException.class,
                () -> parkingSectorService.create(request)
        );

        verify(parkingSectorRepository, never()).save(any());
    }

    @Test
    void shouldReturnParkingSectorById() {

        when(parkingSectorRepository.findById(parkingSector.getId()))
                .thenReturn(Optional.of(parkingSector));

        ParkingSectorResponse response =
                parkingSectorService.getById(parkingSector.getId());

        assertEquals(parkingSector.getId(), response.id());
        assertEquals("A", response.name());
    }

    @Test
    void shouldThrowExceptionWhenParkingSectorDoesNotExist() {

        UUID id = UUID.randomUUID();

        when(parkingSectorRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> parkingSectorService.getById(id)
        );
    }

    @Test
    void shouldReturnAllParkingSectors() {

        Page<ParkingSector> page =
                new PageImpl<>(List.of(parkingSector));

        when(parkingSectorRepository.findAll(PageRequest.of(0, 10)))
                .thenReturn(page);

        Page<ParkingSectorResponse> response =
                parkingSectorService.findAll(PageRequest.of(0, 10));

        assertEquals(1, response.getTotalElements());
        assertEquals("A", response.getContent().getFirst().name());
    }

    @Test
    void shouldUpdateParkingSector() {

        when(parkingSectorRepository.findById(parkingSector.getId()))
                .thenReturn(Optional.of(parkingSector));

        when(parkingSectorRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ParkingSectorRequest updateRequest =
                new ParkingSectorRequest(
                        "B",
                        SectorType.PREMIUM,
                        2
                );

        ParkingSectorResponse response =
                parkingSectorService.update(parkingSector.getId(), updateRequest);

        assertEquals("B", response.name());
        assertEquals(SectorType.PREMIUM, response.type());
        assertEquals(2, response.floor());
    }

    @Test
    void shouldDeactivateParkingSector() {

        when(parkingSectorRepository.findById(parkingSector.getId()))
                .thenReturn(Optional.of(parkingSector));

        when(parkingSectorRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ParkingSectorResponse response =
                parkingSectorService.deactivate(parkingSector.getId());

        assertFalse(response.active());
    }

}
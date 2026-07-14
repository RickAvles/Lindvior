package com.rick.smartparkingplatform.services;

import com.rick.smartparkingplatform.dto.filter.ParkingSessionFilter;
import com.rick.smartparkingplatform.dto.request.ParkingSessionRequest;
import com.rick.smartparkingplatform.dto.response.ParkingSessionResponse;
import com.rick.smartparkingplatform.entity.ParkingSession;
import com.rick.smartparkingplatform.entity.ParkingSpot;
import com.rick.smartparkingplatform.entity.Vehicle;
import com.rick.smartparkingplatform.enums.StatusParkingSession;
import com.rick.smartparkingplatform.enums.StatusParkingSpot;
import com.rick.smartparkingplatform.enums.VehicleType;
import com.rick.smartparkingplatform.exception.*;
import com.rick.smartparkingplatform.repository.ParkingSessionRepository;
import com.rick.smartparkingplatform.repository.ParkingSpotRepository;
import com.rick.smartparkingplatform.repository.VehicleRepository;
import com.rick.smartparkingplatform.service.ParkingSessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingSessionServiceTest {

    @Mock
    private ParkingSessionRepository parkingSessionRepository;

    @Mock
    private ParkingSpotRepository parkingSpotRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private ParkingSessionService parkingSessionService;

    private Vehicle vehicle;
    private ParkingSpot parkingSpot;
    private ParkingSession parkingSession;
    private ParkingSessionRequest request;

    @BeforeEach
    void setUp() {

        vehicle = new Vehicle();
        vehicle.setId(UUID.randomUUID());
        vehicle.setLicensePlate("ABC1D23");
        vehicle.setType(VehicleType.SEDAN);
        vehicle.setColor("Black");
        vehicle.setActive(true);
        vehicle.setCreatedAt(LocalDateTime.now());

        parkingSpot = new ParkingSpot();
        parkingSpot.setId(UUID.randomUUID());
        parkingSpot.setCode("A-001");
        parkingSpot.setStatus(StatusParkingSpot.FREE);
        parkingSpot.setActive(true);

        parkingSession = new ParkingSession();
        parkingSession.setId(UUID.randomUUID());
        parkingSession.setVehicle(vehicle);
        parkingSession.setParkingSpot(parkingSpot);
        parkingSession.setEntryTime(LocalDateTime.now());
        parkingSession.setCreatedAt(LocalDateTime.now());
        parkingSession.setStatus(StatusParkingSession.ACTIVE);

        request = new ParkingSessionRequest(
                "ABC1D23"
        );
    }

    @Test
    void shouldReturnAllParkingSessions() {

        Page<ParkingSession> page =
                new PageImpl<>(List.of(parkingSession));

        when(parkingSessionRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);

        ParkingSessionFilter filter =
                new ParkingSessionFilter(
                        null,
                        null,
                        null,
                        null,
                        null
                );

        Page<ParkingSessionResponse> response =
                parkingSessionService.findAll(PageRequest.of(0, 10), filter);

        assertEquals(1, response.getTotalElements());
        assertEquals(vehicle.getLicensePlate(),
                response.getContent().getFirst().licensePlate());
    }

    @Test
    void shouldReturnParkingSessionById() {

        when(parkingSessionRepository.findById(parkingSession.getId()))
                .thenReturn(Optional.of(parkingSession));

        ParkingSessionResponse response =
                parkingSessionService.getById(parkingSession.getId());

        assertNotNull(response);
        assertEquals(vehicle.getLicensePlate(), response.licensePlate());
        assertEquals(StatusParkingSession.ACTIVE, response.status());
    }

    @Test
    void shouldThrowExceptionWhenParkingSessionDoesNotExist() {

        UUID id = UUID.randomUUID();

        when(parkingSessionRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> parkingSessionService.getById(id)
        );
    }

    @Test
    void shouldCreateParkingSession() {

        when(vehicleRepository.findByLicensePlate(request.licensePlate()))
                .thenReturn(Optional.of(vehicle));

        when(parkingSpotRepository.existsByActiveTrue())
                .thenReturn(true);

        when(parkingSessionRepository.existsByVehicleAndStatus(
                vehicle,
                StatusParkingSession.ACTIVE))
                .thenReturn(false);

        when(parkingSpotRepository.findFirstByStatusAndActiveTrue(StatusParkingSpot.FREE))
                .thenReturn(Optional.of(parkingSpot));

        when(parkingSpotRepository.save(any(ParkingSpot.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(parkingSessionRepository.save(any(ParkingSession.class)))
                .thenAnswer(invocation -> {
                    ParkingSession session = invocation.getArgument(0);
                    session.setId(UUID.randomUUID());
                    return session;
                });

        ParkingSessionResponse response =
                parkingSessionService.create(request);

        assertNotNull(response);
        assertEquals(vehicle.getLicensePlate(), response.licensePlate());
        assertEquals(StatusParkingSession.ACTIVE, response.status());

        ArgumentCaptor<ParkingSession> captor =
                ArgumentCaptor.forClass(ParkingSession.class);

        verify(parkingSessionRepository).save(captor.capture());

        ParkingSession saved = captor.getValue();

        assertEquals(vehicle, saved.getVehicle());
        assertEquals(parkingSpot, saved.getParkingSpot());
        assertEquals(StatusParkingSession.ACTIVE, saved.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenVehicleDoesNotExist() {

        when(vehicleRepository.findByLicensePlate(request.licensePlate()))
                .thenReturn(Optional.empty());

        assertThrows(
                VehicleNotFoundException.class,
                () -> parkingSessionService.create(request)
        );

        verify(parkingSessionRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenVehicleAlreadyHasActiveSession() {

        when(vehicleRepository.findByLicensePlate(request.licensePlate()))
                .thenReturn(Optional.of(vehicle));

        when(parkingSessionRepository.existsByVehicleAndStatus(
                vehicle,
                StatusParkingSession.ACTIVE))
                .thenReturn(true);

        assertThrows(
                OpenParkingSessionAlreadyExistsException.class,
                () -> parkingSessionService.create(request)
        );

        verify(parkingSessionRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenParkingIsUnavailable() {

        when(vehicleRepository.findByLicensePlate(request.licensePlate()))
                .thenReturn(Optional.of(vehicle));

        when(parkingSpotRepository.existsByActiveTrue())
                .thenReturn(false);

        assertThrows(
                ParkingCurrentlyUnavailableException.class,
                () -> parkingSessionService.create(request)
        );

        verify(parkingSessionRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenNoParkingSpotsAreAvailable() {

        when(vehicleRepository.findByLicensePlate(request.licensePlate()))
                .thenReturn(Optional.of(vehicle));

        when(parkingSpotRepository.existsByActiveTrue())
                .thenReturn(true);

        when(parkingSessionRepository.existsByVehicleAndStatus(
                vehicle,
                StatusParkingSession.ACTIVE))
                .thenReturn(false);

        when(parkingSpotRepository.findFirstByStatusAndActiveTrue(StatusParkingSpot.FREE))
                .thenReturn(Optional.empty());

        assertThrows(
                NoParkingSpotsAvailableException.class,
                () -> parkingSessionService.create(request)
        );

        verify(parkingSessionRepository, never()).save(any());
    }

    @Test
    void shouldCloseParkingSession() {

        parkingSpot.setStatus(StatusParkingSpot.OCCUPIED);

        when(parkingSessionRepository.findById(parkingSession.getId()))
                .thenReturn(Optional.of(parkingSession));

        when(parkingSpotRepository.save(any(ParkingSpot.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(parkingSessionRepository.save(any(ParkingSession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ParkingSessionResponse response =
                parkingSessionService.close(parkingSession.getId());

        assertNotNull(response);
        assertEquals(StatusParkingSession.FINISHED, response.status());
        assertNotNull(response.exitTime());

        assertEquals(StatusParkingSpot.FREE, parkingSpot.getStatus());

        verify(parkingSpotRepository).save(parkingSpot);
        verify(parkingSessionRepository).save(parkingSession);
    }

    @Test
    void shouldThrowExceptionWhenClosingNonExistingSession() {

        UUID id = UUID.randomUUID();

        when(parkingSessionRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> parkingSessionService.close(id)
        );

        verify(parkingSessionRepository, never()).save(any());
        verify(parkingSpotRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenParkingSessionIsAlreadyFinished() {

        parkingSession.setStatus(StatusParkingSession.FINISHED);

        when(parkingSessionRepository.findById(parkingSession.getId()))
                .thenReturn(Optional.of(parkingSession));

        assertThrows(
                ParkingAlreadyClosedException.class,
                () -> parkingSessionService.close(parkingSession.getId())
        );

        verify(parkingSessionRepository, never()).save(any());
        verify(parkingSpotRepository, never()).save(any());
    }

}

package com.rick.smartparkingplatform.service;

import com.rick.smartparkingplatform.dto.filter.ParkingSessionFilter;
import com.rick.smartparkingplatform.dto.request.ParkingSessionRequest;
import com.rick.smartparkingplatform.dto.response.ParkingSessionResponse;
import com.rick.smartparkingplatform.entity.ParkingSession;
import com.rick.smartparkingplatform.entity.ParkingSpot;
import com.rick.smartparkingplatform.enums.StatusParkingSession;
import com.rick.smartparkingplatform.enums.StatusParkingSpot;
import com.rick.smartparkingplatform.exception.*;
import com.rick.smartparkingplatform.repository.ParkingSessionRepository;
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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ParkingSessionServiceTest {

    @Mock
    private ParkingSessionRepository parkingSessionRepository;

    @Mock
    private ParkingSpotRepository parkingSpotRepository;

    @InjectMocks
    private ParkingSessionService parkingSessionService;

    @Test
    void createShouldThrowParkingCurrentlyUnavailableException() {

        ParkingSessionRequest request = new ParkingSessionRequest("ABC1D23");

        when(parkingSpotRepository.existsByActiveTrue()).thenReturn(false);

        assertThrows(
                ParkingCurrentlyUnavailableException.class,
                () -> parkingSessionService.create(request)
        );

        verify(parkingSpotRepository).existsByActiveTrue();
        verify(parkingSessionRepository, never())
                .existsByLicensePlateAndStatus(anyString(), any());
    }

    @Test
    void createShouldThrowOpenParkingSessionAlreadyExistsException() {

        ParkingSessionRequest request = new ParkingSessionRequest("ABC1D23");

        when(parkingSpotRepository.existsByActiveTrue()).thenReturn(true);

        when(parkingSessionRepository.existsByLicensePlateAndStatus(
                request.licensePlate(),
                StatusParkingSession.OPEN
        )).thenReturn(true);

        assertThrows(
                OpenParkingSessionAlreadyExistsException.class,
                () -> parkingSessionService.create(request)
        );

        verify(parkingSpotRepository).existsByActiveTrue();

        verify(parkingSessionRepository)
                .existsByLicensePlateAndStatus(
                        request.licensePlate(),
                        StatusParkingSession.OPEN
                );

        verify(parkingSpotRepository, never())
                .findFirstByStatusAndActiveTrue(any());
    }

    @Test
    void createShouldThrowNoParkingSpotsAvailableException() {

        ParkingSessionRequest request = new ParkingSessionRequest("ABC1D23");

        when(parkingSpotRepository.existsByActiveTrue()).thenReturn(true);

        when(parkingSessionRepository.existsByLicensePlateAndStatus(
                request.licensePlate(),
                StatusParkingSession.OPEN
        )).thenReturn(false);

        when(parkingSpotRepository.findFirstByStatusAndActiveTrue(
                StatusParkingSpot.FREE
        )).thenReturn(Optional.empty());

        assertThrows(
                NoParkingSpotsAvailableException.class,
                () -> parkingSessionService.create(request)
        );

        verify(parkingSpotRepository).existsByActiveTrue();

        verify(parkingSessionRepository)
                .existsByLicensePlateAndStatus(
                        request.licensePlate(),
                        StatusParkingSession.OPEN
                );

        verify(parkingSpotRepository)
                .findFirstByStatusAndActiveTrue(StatusParkingSpot.FREE);

        verify(parkingSpotRepository, never())
                .save(any());

        verify(parkingSessionRepository, never())
                .save(any());
    }


    @Test
    void createShouldReturnParkingSession() {

        ParkingSessionRequest request = new ParkingSessionRequest("ABC1D23");

        when(parkingSpotRepository.existsByActiveTrue()).thenReturn(true);

        when(parkingSessionRepository.existsByLicensePlateAndStatus(
                request.licensePlate(),
                StatusParkingSession.OPEN
        )).thenReturn(false);

        ParkingSpot parkingSpot = new ParkingSpot();
        parkingSpot.setId(UUID.randomUUID());
        parkingSpot.setCode("A-01");
        parkingSpot.setStatus(StatusParkingSpot.FREE);
        parkingSpot.setActive(true);
        parkingSpot.setCreatedAt(LocalDateTime.now());

        when(parkingSpotRepository.findFirstByStatusAndActiveTrue(
                StatusParkingSpot.FREE
        )).thenReturn(Optional.of(parkingSpot));

        ParkingSession parkingSession = new ParkingSession();
        parkingSession.setId(UUID.randomUUID());
        parkingSession.setLicensePlate(request.licensePlate());
        parkingSession.setStatus(StatusParkingSession.OPEN);
        parkingSession.setEntryTime(LocalDateTime.now());
        parkingSession.setCreatedAt(LocalDateTime.now());
        parkingSession.setParkingSpot(parkingSpot);

        when(parkingSpotRepository.save(any(ParkingSpot.class)))
                .thenReturn(parkingSpot);

        when(parkingSessionRepository.save(any(ParkingSession.class)))
                .thenReturn(parkingSession);

        ParkingSessionResponse response = parkingSessionService.create(request);

        assertNotNull(response);
        assertEquals("ABC1D23", response.licensePlate());
        assertEquals(StatusParkingSession.OPEN, response.status());
        assertEquals("A-01", response.parkingSpotCode());

        assertEquals(StatusParkingSpot.OCCUPIED, parkingSpot.getStatus());

        verify(parkingSpotRepository).existsByActiveTrue();

        verify(parkingSessionRepository)
                .existsByLicensePlateAndStatus(
                        request.licensePlate(),
                        StatusParkingSession.OPEN
                );

        verify(parkingSpotRepository)
                .findFirstByStatusAndActiveTrue(StatusParkingSpot.FREE);

        verify(parkingSpotRepository)
                .save(any(ParkingSpot.class));

        verify(parkingSessionRepository)
                .save(any(ParkingSession.class));
    }

    @Test
    void getByIdShouldThrowResourceNotFoundException() {

        UUID id = UUID.randomUUID();

        when(parkingSessionRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> parkingSessionService.getById(id)
        );

        verify(parkingSessionRepository).findById(id);
    }

    @Test
    void getByIdShouldReturnParkingSession() {

        UUID id = UUID.randomUUID();

        ParkingSpot parkingSpot = new ParkingSpot();
        parkingSpot.setCode("A-01");

        ParkingSession parkingSession = new ParkingSession();
        parkingSession.setId(id);
        parkingSession.setLicensePlate("ABC1D23");
        parkingSession.setStatus(StatusParkingSession.OPEN);
        parkingSession.setEntryTime(LocalDateTime.now());
        parkingSession.setCreatedAt(LocalDateTime.now());
        parkingSession.setParkingSpot(parkingSpot);

        when(parkingSessionRepository.findById(id))
                .thenReturn(Optional.of(parkingSession));

        ParkingSessionResponse response = parkingSessionService.getById(id);

        assertNotNull(response);
        assertEquals(id, response.id());
        assertEquals("ABC1D23", response.licensePlate());
        assertEquals(StatusParkingSession.OPEN, response.status());
        assertEquals("A-01", response.parkingSpotCode());

        verify(parkingSessionRepository).findById(id);
    }

    @Test
    void closeShouldThrowResourceNotFoundException() {

        UUID id = UUID.randomUUID();

        when(parkingSessionRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> parkingSessionService.close(id)
        );

        verify(parkingSessionRepository).findById(id);
    }

    @Test
    void closeShouldThrowParkingAlreadyClosedException() {

        UUID id = UUID.randomUUID();

        ParkingSession parkingSession = new ParkingSession();
        parkingSession.setStatus(StatusParkingSession.CLOSED);

        when(parkingSessionRepository.findById(id))
                .thenReturn(Optional.of(parkingSession));

        assertThrows(
                ParkingAlreadyClosedException.class,
                () -> parkingSessionService.close(id)
        );

        verify(parkingSessionRepository).findById(id);

        verify(parkingSpotRepository, never())
                .save(any());

        verify(parkingSessionRepository, never())
                .save(any(ParkingSession.class));
    }

    @Test
    void closeShouldReturnClosedParkingSession() {

        UUID id = UUID.randomUUID();

        ParkingSpot parkingSpot = new ParkingSpot();
        parkingSpot.setId(UUID.randomUUID());
        parkingSpot.setCode("A-01");
        parkingSpot.setStatus(StatusParkingSpot.OCCUPIED);
        parkingSpot.setActive(true);
        parkingSpot.setCreatedAt(LocalDateTime.now());

        ParkingSession parkingSession = new ParkingSession();
        parkingSession.setId(id);
        parkingSession.setLicensePlate("ABC1D23");
        parkingSession.setStatus(StatusParkingSession.OPEN);
        parkingSession.setEntryTime(LocalDateTime.now());
        parkingSession.setCreatedAt(LocalDateTime.now());
        parkingSession.setParkingSpot(parkingSpot);

        when(parkingSessionRepository.findById(id))
                .thenReturn(Optional.of(parkingSession));

        when(parkingSpotRepository.save(any(ParkingSpot.class)))
                .thenReturn(parkingSpot);

        when(parkingSessionRepository.save(any(ParkingSession.class)))
                .thenReturn(parkingSession);

        ParkingSessionResponse response = parkingSessionService.close(id);

        assertNotNull(response);
        assertEquals("ABC1D23", response.licensePlate());
        assertEquals(StatusParkingSession.CLOSED, response.status());
        assertEquals("A-01", response.parkingSpotCode());

        assertEquals(StatusParkingSpot.FREE, parkingSpot.getStatus());
        assertEquals(StatusParkingSession.CLOSED, parkingSession.getStatus());
        assertNotNull(parkingSession.getExitTime());

        verify(parkingSessionRepository).findById(id);

        verify(parkingSpotRepository)
                .save(any(ParkingSpot.class));

        verify(parkingSessionRepository)
                .save(any(ParkingSession.class));
    }

    @Test
    void findAllShouldReturnParkingSessionPage() {

        Pageable pageable = PageRequest.of(0, 20);

        ParkingSessionFilter filter = new ParkingSessionFilter(
                null,
                null,
                null,
                null,
                null
        );

        ParkingSpot parkingSpot = new ParkingSpot();
        parkingSpot.setCode("A-01");

        ParkingSession parkingSession = new ParkingSession();
        parkingSession.setId(UUID.randomUUID());
        parkingSession.setLicensePlate("ABC1D23");
        parkingSession.setStatus(StatusParkingSession.OPEN);
        parkingSession.setEntryTime(LocalDateTime.now());
        parkingSession.setCreatedAt(LocalDateTime.now());
        parkingSession.setParkingSpot(parkingSpot);

        Page<ParkingSession> page = new PageImpl<>(
                List.of(parkingSession),
                pageable,
                1
        );

        when(parkingSessionRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(page);

        Page<ParkingSessionResponse> response =
                parkingSessionService.findAll(pageable, filter);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getContent().size());

        ParkingSessionResponse session = response.getContent().getFirst();

        assertEquals("ABC1D23", session.licensePlate());
        assertEquals(StatusParkingSession.OPEN, session.status());
        assertEquals("A-01", session.parkingSpotCode());

        verify(parkingSessionRepository)
                .findAll(any(Specification.class), any(Pageable.class));
    }

}

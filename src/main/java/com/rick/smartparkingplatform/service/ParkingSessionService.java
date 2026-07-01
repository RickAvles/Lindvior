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
import com.rick.smartparkingplatform.specification.ParkingSessionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParkingSessionService {

    private final ParkingSessionRepository parkingSessionRepository;
    private final ParkingSpotRepository parkingSpotRepository;

    private ParkingSession toEntity(ParkingSessionRequest request, ParkingSpot spot) {
        ParkingSession parkingSession = new ParkingSession();
        LocalDateTime now = LocalDateTime.now();

        parkingSession.setLicensePlate(request.licensePlate());
        parkingSession.setEntryTime(now);
        parkingSession.setCreatedAt(now);
        parkingSession.setStatus(StatusParkingSession.OPEN);
        parkingSession.setParkingSpot(spot);

        return parkingSession;
    }

    private ParkingSessionResponse toResponse(ParkingSession session) {
        return new ParkingSessionResponse(
                session.getId(),
                session.getLicensePlate(),
                session.getEntryTime(),
                session.getStatus(),
                session.getParkingSpot().getCode(),
                session.getCreatedAt()
        );
    }

    private Specification<ParkingSession> buildSpecification(ParkingSessionFilter filter) {

        Specification<ParkingSession> specification = Specification.unrestricted();

        if (filter.licensePlate() != null) {
            specification = specification.and(ParkingSessionSpecification.hasLicensePlate(filter.licensePlate()));
        }

        if (filter.status() != null) {
            specification = specification.and(ParkingSessionSpecification.hasStatus(filter.status()));
        }

        if (filter.parkingSpotCode() != null) {
            specification = specification.and(ParkingSessionSpecification.hasParkingSpotCode(filter.parkingSpotCode()));
        }

        if (filter.startDate() != null) {
            specification = specification.and(ParkingSessionSpecification.hasEntryTimeAfter(filter.startDate()));
        }

        if (filter.endDate() != null) {
            specification = specification.and(ParkingSessionSpecification.hasEntryTimeBefore(filter.endDate()));
        }

        return specification;
    }

    private ParkingSession findParkingSessionById(UUID id) {
        return parkingSessionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Parking spot not found."));
    }

    public Page<ParkingSessionResponse> findAll(Pageable pageable, ParkingSessionFilter filter) {

        Specification<ParkingSession> specification = buildSpecification(filter);

        Page<ParkingSession> response = parkingSessionRepository.findAll(specification, pageable);

        return response.map(this::toResponse);
    }

    public ParkingSessionResponse create(ParkingSessionRequest request) {

        if (!parkingSpotRepository.existsByActiveTrue()) {
            throw new ParkingCurrentlyUnavailableException();
        }

        if (parkingSessionRepository.existsByLicensePlateAndStatus(request.licensePlate(), StatusParkingSession.OPEN)) {
            throw new OpenParkingSessionAlreadyExistsException();
        }

        ParkingSpot spot = parkingSpotRepository.findFirstByStatusAndActiveTrue(StatusParkingSpot.FREE).orElseThrow(NoParkingSpotsAvailableException::new);

        spot.setStatus(StatusParkingSpot.OCCUPIED);

        ParkingSession session = toEntity(request, spot);

        parkingSpotRepository.save(spot);
        ParkingSession sessionSaved = parkingSessionRepository.save(session);

        return toResponse(sessionSaved);
    }

    public ParkingSessionResponse close(UUID id) {

        ParkingSession session = findParkingSessionById(id);
        ParkingSpot parkingSpot = session.getParkingSpot();

        if (session.getStatus() != StatusParkingSession.OPEN) {
            throw new ParkingAlreadyClosedException();
        }

        session.setExitTime(LocalDateTime.now());
        session.setStatus(StatusParkingSession.CLOSED);
        parkingSpot.setStatus(StatusParkingSpot.FREE);

        parkingSpotRepository.save(parkingSpot);
        ParkingSession sessionSaved = parkingSessionRepository.save(session);

        return toResponse(sessionSaved);
    }

    public ParkingSessionResponse getById(UUID id) {
        ParkingSession session = findParkingSessionById(id);

        return toResponse(session);
    }

}

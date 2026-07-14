package com.rick.smartparkingplatform.service;

import com.rick.smartparkingplatform.dto.filter.ParkingSessionFilter;
import com.rick.smartparkingplatform.dto.request.ParkingSessionRequest;
import com.rick.smartparkingplatform.dto.response.ParkingSessionResponse;
import com.rick.smartparkingplatform.entity.ParkingSession;
import com.rick.smartparkingplatform.entity.ParkingSpot;
import com.rick.smartparkingplatform.entity.Vehicle;
import com.rick.smartparkingplatform.enums.StatusParkingSession;
import com.rick.smartparkingplatform.enums.StatusParkingSpot;
import com.rick.smartparkingplatform.exception.*;
import com.rick.smartparkingplatform.repository.ParkingSessionRepository;
import com.rick.smartparkingplatform.repository.ParkingSpotRepository;
import com.rick.smartparkingplatform.repository.VehicleRepository;
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
    private final VehicleRepository vehicleRepository;

    /**
     * Converte os dados necessários para uma entidade ParkingSession.
     */
    private ParkingSession toEntity(Vehicle vehicle, ParkingSpot parkingSpot) {

        ParkingSession parkingSession = new ParkingSession();

        LocalDateTime now = LocalDateTime.now();

        parkingSession.setVehicle(vehicle);
        parkingSession.setParkingSpot(parkingSpot);
        parkingSession.setEntryTime(now);
        parkingSession.setCreatedAt(now);
        parkingSession.setStatus(StatusParkingSession.ACTIVE);

        return parkingSession;
    }

    /**
     * Converte uma entidade ParkingSession para o DTO de resposta.
     */
    private ParkingSessionResponse toResponse(ParkingSession session) {

        return new ParkingSessionResponse(
                session.getId(),
                session.getVehicle().getLicensePlate(),
                session.getVehicle().getType(),
                session.getEntryTime(),
                session.getExitTime(),
                session.getStatus(),
                session.getParkingSpot().getCode(),
                session.getCreatedAt()
        );
    }

    /**
     * Monta dinamicamente os filtros da consulta.
     */
    private Specification<ParkingSession> buildSpecification(ParkingSessionFilter filter) {

        Specification<ParkingSession> specification = Specification.unrestricted();

        if (filter.licensePlate() != null) {
            specification = specification.and(
                    ParkingSessionSpecification.hasLicensePlate(filter.licensePlate())
            );
        }

        if (filter.status() != null) {
            specification = specification.and(
                    ParkingSessionSpecification.hasStatus(filter.status())
            );
        }

        if (filter.parkingSpotCode() != null) {
            specification = specification.and(
                    ParkingSessionSpecification.hasParkingSpotCode(filter.parkingSpotCode())
            );
        }

        if (filter.startDate() != null) {
            specification = specification.and(
                    ParkingSessionSpecification.hasEntryTimeAfter(filter.startDate())
            );
        }

        if (filter.endDate() != null) {
            specification = specification.and(
                    ParkingSessionSpecification.hasExitTimeBefore(filter.endDate())
            );
        }

        return specification;
    }

    /**
     * Busca uma sessão pelo identificador.
     */
    private ParkingSession findParkingSessionById(UUID id) {

        return parkingSessionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Parking session not found."));
    }

    /**
     * Lista sessões aplicando os filtros informados.
     */
    public Page<ParkingSessionResponse> findAll(Pageable pageable, ParkingSessionFilter filter) {

        Specification<ParkingSession> specification = buildSpecification(filter);

        return parkingSessionRepository
                .findAll(specification, pageable)
                .map(this::toResponse);
    }

    /**
     * Abre uma nova sessão de estacionamento.
     */
    public ParkingSessionResponse create(ParkingSessionRequest request) {

        Vehicle vehicle = vehicleRepository.findByLicensePlate(request.licensePlate()).orElseThrow(VehicleNotFoundException::new);

        if (parkingSessionRepository.existsByVehicleAndStatus(
                vehicle,
                StatusParkingSession.ACTIVE)) {

            throw new OpenParkingSessionAlreadyExistsException();
        }

        if (!parkingSpotRepository.existsByActiveTrue()) {
            throw new ParkingCurrentlyUnavailableException();
        }
        
        ParkingSpot parkingSpot = parkingSpotRepository
                .findFirstByStatusAndActiveTrue(StatusParkingSpot.FREE)
                .orElseThrow(NoParkingSpotsAvailableException::new);

        parkingSpot.setStatus(StatusParkingSpot.OCCUPIED);

        ParkingSession parkingSession = toEntity(vehicle, parkingSpot);

        parkingSpotRepository.save(parkingSpot);

        ParkingSession savedParkingSession =
                parkingSessionRepository.save(parkingSession);

        return toResponse(savedParkingSession);
    }

    /**
     * Finaliza uma sessão de estacionamento.
     */
    public ParkingSessionResponse close(UUID id) {

        ParkingSession parkingSession = findParkingSessionById(id);
        ParkingSpot parkingSpot = parkingSession.getParkingSpot();

        if (parkingSession.getStatus() != StatusParkingSession.ACTIVE) {
            throw new ParkingAlreadyClosedException();
        }

        parkingSession.setExitTime(LocalDateTime.now());
        parkingSession.setStatus(StatusParkingSession.FINISHED);

        parkingSpot.setStatus(StatusParkingSpot.FREE);

        parkingSpotRepository.save(parkingSpot);

        ParkingSession savedParkingSession =
                parkingSessionRepository.save(parkingSession);

        return toResponse(savedParkingSession);
    }

    /**
     * Busca uma sessão pelo identificador.
     */
    public ParkingSessionResponse getById(UUID id) {

        ParkingSession parkingSession = findParkingSessionById(id);

        return toResponse(parkingSession);
    }

}
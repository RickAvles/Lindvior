package com.rick.smartparkingplatform.service;

import com.rick.smartparkingplatform.dto.filter.ParkingSessionFilter;
import com.rick.smartparkingplatform.dto.response.ParkingSessionResponse;
import com.rick.smartparkingplatform.entity.ParkingSession;
import com.rick.smartparkingplatform.entity.ParkingSpot;
import com.rick.smartparkingplatform.entity.Vehicle;
import com.rick.smartparkingplatform.enums.StatusParkingSession;
import com.rick.smartparkingplatform.exception.OpenParkingSessionAlreadyExistsException;
import com.rick.smartparkingplatform.exception.ParkingAlreadyClosedException;
import com.rick.smartparkingplatform.exception.ResourceNotFoundException;
import com.rick.smartparkingplatform.repository.ParkingSessionRepository;
import com.rick.smartparkingplatform.specification.ParkingSessionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParkingSessionService {

    private final ParkingSessionRepository parkingSessionRepository;

    // =====================================================
    // API
    // =====================================================

    // Converte os dados necessários para uma entidade ParkingSession.
    private ParkingSession requestToEntity(Vehicle vehicle, ParkingSpot parkingSpot) {

        ParkingSession parkingSession = new ParkingSession();

        LocalDateTime now = LocalDateTime.now();

        parkingSession.setVehicle(vehicle);
        parkingSession.setParkingSpot(parkingSpot);
        parkingSession.setEntryTime(now);
        parkingSession.setCreatedAt(now);
        parkingSession.setStatus(StatusParkingSession.ENTERING);

        return parkingSession;
    }

    // Converte uma entidade ParkingSession para o DTO de resposta.
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

    // Monta dinamicamente os filtros da consulta.
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

    // Busca uma sessão pelo identificador.
    private ParkingSession findParkingSessionById(UUID id) {

        return parkingSessionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Parking session not found."));
    }

    // Lista sessões aplicando os filtros informados.
    public Page<ParkingSessionResponse> findAll(Pageable pageable, ParkingSessionFilter filter) {

        Specification<ParkingSession> specification = buildSpecification(filter);

        return parkingSessionRepository.findAll(specification, pageable).map(this::toResponse);
    }

    // Retorna a entidade da sessão.
    public ParkingSession getEntity(UUID id) {

        return findParkingSessionById(id);
    }

    // Retorna uma sessão pelo identificador.
    public ParkingSessionResponse getById(UUID id) {

        ParkingSession parkingSession = findParkingSessionById(id);

        return toResponse(parkingSession);
    }

    // =====================================================
    // SIMULAÇÃO
    // =====================================================

    // Valida se a sessão está ativa.
    public void validateActiveSession(ParkingSession parkingSession) {

        if (parkingSession.getStatus() != StatusParkingSession.ACTIVE) {
            throw new ParkingAlreadyClosedException();
        }
    }

    // Valida se o veículo não possui uma sessão aberta.
    public void validateNoOpenSession(Vehicle vehicle) {

        if (existsOpenSession(vehicle.getId())) {
            throw new OpenParkingSessionAlreadyExistsException();
        }
    }

    // Cria e persiste uma nova sessão de estacionamento.
    public ParkingSession startEntering(Vehicle vehicle, ParkingSpot parkingSpot) {

        ParkingSession parkingSession = requestToEntity(vehicle, parkingSpot);

        return parkingSessionRepository.save(parkingSession);
    }

    // Atualiza o status da sessão.
    private void updateStatus(ParkingSession parkingSession, StatusParkingSession status) {

        parkingSession.setStatus(status);

        parkingSessionRepository.save(parkingSession);
    }

    // Marca a sessão como estacionada.
    public void park(ParkingSession parkingSession) {

        updateStatus(parkingSession, StatusParkingSession.ACTIVE);
    }

    // Marca a sessão como em processo de saída.
    public void startExit(ParkingSession parkingSession) {

        updateStatus(parkingSession, StatusParkingSession.EXITING);
    }

    // Finaliza uma sessão de estacionamento.
    public void closeSession(
            ParkingSession parkingSession,
            LocalDateTime currentTime) {

        parkingSession.setExitTime(currentTime);

        updateStatus(parkingSession, StatusParkingSession.FINISHED);
    }

    // Retorna todas as sessões em processo de entrada.
    public List<ParkingSession> getEnteringSessions() {

        return parkingSessionRepository.findByStatus(StatusParkingSession.ENTERING);
    }

    // Retorna todas as sessões ativas.
    public List<ParkingSession> getActiveSessions() {

        return parkingSessionRepository.findByStatus(StatusParkingSession.ACTIVE);
    }

    // Verifica se o veículo possui uma sessão aberta.
    public boolean existsOpenSession(UUID vehicleId) {

        return parkingSessionRepository.existsOpenSession(vehicleId);
    }

    // =====================================================
    // RELATÓRIOS
    // =====================================================

    // Nenhum méto do por enquanto.

}
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
import com.rick.smartparkingplatform.mapper.ParkingSessionMapper;
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
    private final ParkingSessionMapper mapper;

    // =====================================================
    // API
    // =====================================================

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

        return parkingSessionRepository.findAll(specification, pageable).map(mapper::toResponse);
    }

    // Retorna uma sessão pelo identificador.
    public ParkingSessionResponse getById(UUID id) {

        ParkingSession parkingSession = findParkingSessionById(id);

        return mapper.toResponse(parkingSession);
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

        ParkingSession parkingSession = mapper.toEntity(vehicle, parkingSpot);

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

    // Traz uma sessão não concluida de volta para ativa.
    public void restoreActive(ParkingSession parkingSession) {

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

    public List<ParkingSession> getExitingSessions() {

        return parkingSessionRepository.findByStatus(StatusParkingSession.EXITING);
    }

    // Retorna todas as sessões ativas.
    public List<ParkingSession> getActiveSessions() {

        return parkingSessionRepository.findByStatus(StatusParkingSession.ACTIVE);
    }

    // Retorna todas as sessões ativas para a dashboard.
    public List<ParkingSession> getActiveSessionsDashboard() {

        return parkingSessionRepository.findByStatusFetchVehicleAndSpot(StatusParkingSession.ACTIVE);
    }

    // Verifica se o veículo possui uma sessão aberta.
    public boolean existsOpenSession(UUID vehicleId) {

        return parkingSessionRepository.existsOpenSession(vehicleId);
    }

    // Retorna a quantidade de sessões em processo de entrada.
    public long countEnteringSessions() {

        return parkingSessionRepository.countByStatus(StatusParkingSession.ENTERING);
    }

    // Retorna a quantidade de sessões ativas.
    public long countActiveSessions() {

        return parkingSessionRepository.countByStatus(StatusParkingSession.ACTIVE);
    }

    // Retorna a quantidade de sessões em processo de saída.
    public long countExitingSessions() {

        return parkingSessionRepository.countByStatus(StatusParkingSession.EXITING);
    }

    // Retorna a quantidade de sessões finalizadas.
    public long countCompletedSessions() {

        return parkingSessionRepository.countByStatus(StatusParkingSession.FINISHED);
    }

    // =====================================================
    // RELATÓRIOS
    // =====================================================

    // Nenhum méto do por enquanto.

}
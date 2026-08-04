package com.rick.smartparkingplatform.service;

import com.rick.smartparkingplatform.dto.filter.ParkingSpotFilter;
import com.rick.smartparkingplatform.dto.request.ParkingSpotRequest;
import com.rick.smartparkingplatform.dto.response.OccupancyResponse;
import com.rick.smartparkingplatform.dto.response.ParkingSpotResponse;
import com.rick.smartparkingplatform.entity.ParkingSector;
import com.rick.smartparkingplatform.entity.ParkingSpot;
import com.rick.smartparkingplatform.enums.StatusParkingSpot;
import com.rick.smartparkingplatform.exception.NoParkingSpotsAvailableException;
import com.rick.smartparkingplatform.exception.ParkingSpotAlreadyExistsException;
import com.rick.smartparkingplatform.exception.ResourceNotFoundException;
import com.rick.smartparkingplatform.mapper.ParkingSpotMapper;
import com.rick.smartparkingplatform.repository.ParkingSectorRepository;
import com.rick.smartparkingplatform.repository.ParkingSpotRepository;
import com.rick.smartparkingplatform.simulation.metrics.statistics.ParkingOccupancy;
import com.rick.smartparkingplatform.specification.ParkingSpotSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParkingSpotService {

    private final ParkingSpotRepository parkingSpotRepository;
    private final ParkingSectorRepository parkingSectorRepository;
    private final ParkingSpotMapper mapper;

    // =====================================================
    // API
    // =====================================================

    // Monta dinamicamente os filtros da consulta.
    private Specification<ParkingSpot> buildSpecification(ParkingSpotFilter filter) {

        Specification<ParkingSpot> specification = Specification.unrestricted();

        if (filter.floor() != null) {
            specification = specification.and(
                    ParkingSpotSpecification.hasFloor(filter.floor())
            );
        }

        if (filter.sector() != null) {
            specification = specification.and(
                    ParkingSpotSpecification.hasSector(filter.sector())
            );
        }

        if (filter.status() != null) {
            specification = specification.and(
                    ParkingSpotSpecification.hasStatus(filter.status())
            );
        }

        if (filter.active() != null) {
            specification = specification.and(
                    ParkingSpotSpecification.hasActive(filter.active())
            );
        }

        if (filter.parkingSectorType() != null) {
            specification = specification.and(
                    ParkingSpotSpecification.hasSectorType(filter.parkingSectorType())
            );
        }

        if (filter.parkingSpotType() != null) {
            specification = specification.and(
                    ParkingSpotSpecification.hasSpotType(filter.parkingSpotType())
            );
        }

        return specification;
    }

    // Busca as vagas aplicando os filtros informados.
    public Page<ParkingSpotResponse> findAll(Pageable pageable, ParkingSpotFilter filter) {

        Specification<ParkingSpot> specification = buildSpecification(filter);

        return parkingSpotRepository
                .findAll(specification, pageable)
                .map(mapper::toResponse);
    }

    // Cria uma nova vaga vinculada a um setor.
    public ParkingSpotResponse create(ParkingSpotRequest request) {

        ParkingSector parkingSector = parkingSectorRepository.findById(request.parkingSectorId())
                .orElseThrow(() -> new ResourceNotFoundException("Parking sector not found."));

        ParkingSpot parkingSpot = mapper.toEntity(
                request,
                parkingSector
        );

        if (parkingSpotRepository.existsByCodeAndParkingSector(
                parkingSpot.getCode(),
                parkingSpot.getParkingSector())) {

            throw new ParkingSpotAlreadyExistsException();
        }

        ParkingSpot savedParkingSpot = parkingSpotRepository.save(parkingSpot);

        return mapper.toResponse(savedParkingSpot);
    }

    // Retorna todas as vagas ativas.
    public List<ParkingSpot> findAllActive() {

        return parkingSpotRepository.findByActiveTrueOrderByParkingSectorNameAscCodeAsc();

    }

    // =====================================================
    // SIMULAÇÃO
    // =====================================================

    // Verifica se existe alguma vaga disponível.
    public boolean hasAvailableSpot() {

        return parkingSpotRepository.existsByStatusAndActiveTrue(StatusParkingSpot.FREE);
    }

    // Retorna a capacidade atual do estacionamento.
    public long getCapacity() {

        return parkingSpotRepository.countByActiveTrue();

    }

    // Retorna a taxa de ocupação do estacionamento.
    public double getOccupancyRate() {

        long totalSpots = parkingSpotRepository.countByActiveTrue();

        if (totalSpots == 0) {
            return 0.0;
        }

        long occupiedSpots = totalSpots
                - parkingSpotRepository.countByStatusAndActiveTrue(
                StatusParkingSpot.FREE
        );

        return (double) occupiedSpots / totalSpots;
    }

    // Reserva a próxima vaga disponível.
    public ParkingSpot reserveAvailableSpot() {

        ParkingSpot parkingSpot = findAvailableSpot();

        updateStatus(parkingSpot, StatusParkingSpot.RESERVED);

        return parkingSpot;
    }

    // Busca a próxima vaga livre.
    private ParkingSpot findAvailableSpot() {

        return parkingSpotRepository
                .findNextAvailableSpot()
                .orElseThrow(NoParkingSpotsAvailableException::new);
    }

    // Ocupa uma vaga.
    public void occupy(ParkingSpot parkingSpot) {

        updateStatus(parkingSpot, StatusParkingSpot.OCCUPIED);
    }

    // Libera uma vaga.
    public void release(ParkingSpot parkingSpot) {

        updateStatus(parkingSpot, StatusParkingSpot.FREE);
    }

    // Atualiza o status da vaga.
    private void updateStatus(ParkingSpot parkingSpot, StatusParkingSpot status) {

        parkingSpot.setStatus(status);

        parkingSpotRepository.save(parkingSpot);
    }

    // =====================================================
    // RELATÓRIOS
    // =====================================================

    // Constrói o estado de ocupação do estacionamento.
    private ParkingOccupancy toParkingOccupancy(
            long totalSpots,
            long availableSpots,
            long occupiedSpots,
            BigDecimal occupancyRate) {

        return new ParkingOccupancy(
                totalSpots,
                availableSpots,
                occupiedSpots,
                occupancyRate
        );
    }

    // Calcula o estado atual de ocupação do estacionamento.
    public ParkingOccupancy getParkingOccupancy() {

        long totalSpots = parkingSpotRepository.countByActiveTrue();

        if (totalSpots == 0) {
            return toParkingOccupancy(
                    0,
                    0,
                    0,
                    BigDecimal.ZERO
            );
        }

        long availableSpots =
                parkingSpotRepository.countByStatusAndActiveTrue(
                        StatusParkingSpot.FREE
                );

        long reservedSpots =
                parkingSpotRepository.countByStatusAndActiveTrue(
                        StatusParkingSpot.RESERVED
                );

        long occupiedSpots =
                parkingSpotRepository.countByStatusAndActiveTrue(
                        StatusParkingSpot.OCCUPIED
                );

        long unavailableSpots = reservedSpots + occupiedSpots;

        double occupancy =
                ((double) unavailableSpots / (double) totalSpots) * 100;

        BigDecimal occupancyRate = BigDecimal.valueOf(occupancy)
                .setScale(2, RoundingMode.HALF_UP);

        return toParkingOccupancy(
                totalSpots,
                availableSpots,
                unavailableSpots,
                occupancyRate
        );
    }

    // Calcula a ocupação atual do estacionamento.
    public OccupancyResponse getOccupancy() {

        ParkingOccupancy occupancy = getParkingOccupancy();

        return new OccupancyResponse(
                occupancy.totalSpots(),
                occupancy.availableSpots(),
                occupancy.occupiedSpots(),
                occupancy.occupancyRate()
        );
    }

}
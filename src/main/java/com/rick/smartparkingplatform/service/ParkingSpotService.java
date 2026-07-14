package com.rick.smartparkingplatform.service;

import com.rick.smartparkingplatform.dto.filter.ParkingSpotFilter;
import com.rick.smartparkingplatform.dto.request.ParkingSpotRequest;
import com.rick.smartparkingplatform.dto.response.OccupancyResponse;
import com.rick.smartparkingplatform.dto.response.ParkingSpotResponse;
import com.rick.smartparkingplatform.entity.ParkingSector;
import com.rick.smartparkingplatform.entity.ParkingSpot;
import com.rick.smartparkingplatform.enums.StatusParkingSpot;
import com.rick.smartparkingplatform.exception.ParkingSpotAlreadyExistsException;
import com.rick.smartparkingplatform.exception.ResourceNotFoundException;
import com.rick.smartparkingplatform.repository.ParkingSectorRepository;
import com.rick.smartparkingplatform.repository.ParkingSpotRepository;
import com.rick.smartparkingplatform.specification.ParkingSpotSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParkingSpotService {

    private final ParkingSpotRepository parkingSpotRepository;
    private final ParkingSectorRepository parkingSectorRepository;

    /**
     * Converte o DTO de criação em uma entidade ParkingSpot.
     */
    private ParkingSpot requestToEntity(ParkingSpotRequest request) {

        ParkingSector parkingSector = parkingSectorRepository.findById(request.parkingSectorId())
                .orElseThrow(() -> new ResourceNotFoundException("Parking sector not found."));

        ParkingSpot parkingSpot = new ParkingSpot();

        parkingSpot.setCode(request.code());
        parkingSpot.setStatus(StatusParkingSpot.FREE);
        parkingSpot.setActive(true);
        parkingSpot.setCreatedAt(LocalDateTime.now());
        parkingSpot.setParkingSector(parkingSector);

        return parkingSpot;
    }

    /**
     * Converte uma entidade ParkingSpot para o DTO de resposta.
     */
    private ParkingSpotResponse entityToResponse(ParkingSpot parkingSpot) {

        ParkingSector sector = parkingSpot.getParkingSector();

        return new ParkingSpotResponse(
                parkingSpot.getId(),
                parkingSpot.getCode(),
                sector.getName(),
                sector.getType(),
                sector.getFloor(),
                parkingSpot.getStatus(),
                parkingSpot.isActive(),
                parkingSpot.getCreatedAt()
        );
    }

    /**
     * Monta dinamicamente os filtros da consulta.
     */
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

        if (filter.sectorType() != null) {
            specification = specification.and(
                    ParkingSpotSpecification.hasSectorType(filter.sectorType())
            );
        }

        return specification;
    }

    /**
     * Constrói a resposta de ocupação do estacionamento.
     */
    private OccupancyResponse toOccupancy(
            Long totalSpots,
            Long availableSpots,
            Long occupiedSpots,
            BigDecimal occupancyRate) {

        return new OccupancyResponse(
                totalSpots,
                availableSpots,
                occupiedSpots,
                occupancyRate
        );
    }

    /**
     * Lista as vagas aplicando os filtros informados.
     */
    public Page<ParkingSpotResponse> findAll(Pageable pageable, ParkingSpotFilter filter) {

        Specification<ParkingSpot> specification = buildSpecification(filter);

        return parkingSpotRepository
                .findAll(specification, pageable)
                .map(this::entityToResponse);
    }

    /**
     * Cria uma nova vaga vinculada a um setor.
     */
    public ParkingSpotResponse create(ParkingSpotRequest request) {

        ParkingSpot parkingSpot = requestToEntity(request);

        if (parkingSpotRepository.existsByCodeAndParkingSector(
                parkingSpot.getCode(),
                parkingSpot.getParkingSector())) {

            throw new ParkingSpotAlreadyExistsException();
        }

        ParkingSpot savedParkingSpot = parkingSpotRepository.save(parkingSpot);

        return entityToResponse(savedParkingSpot);
    }

    /**
     * Calcula a ocupação atual do estacionamento.
     */
    public OccupancyResponse getOccupancy() {

        Long totalSpots = parkingSpotRepository.countByActiveTrue();

        if (totalSpots == 0) {
            return toOccupancy(
                    0L,
                    0L,
                    0L,
                    BigDecimal.ZERO
            );
        }

        Long availableSpots = parkingSpotRepository.countByStatusAndActiveTrue(StatusParkingSpot.FREE);
        Long occupiedSpots = parkingSpotRepository.countByStatusAndActiveTrue(StatusParkingSpot.OCCUPIED);

        double occupancy = (occupiedSpots.doubleValue() / totalSpots.doubleValue()) * 100;

        BigDecimal occupancyRate = BigDecimal.valueOf(occupancy)
                .setScale(2, RoundingMode.HALF_UP);

        return toOccupancy(
                totalSpots,
                availableSpots,
                occupiedSpots,
                occupancyRate
        );
    }
}
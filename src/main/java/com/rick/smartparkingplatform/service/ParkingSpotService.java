package com.rick.smartparkingplatform.service;

import com.rick.smartparkingplatform.dto.filter.ParkingSpotFilter;
import com.rick.smartparkingplatform.dto.request.ParkingSpotRequest;
import com.rick.smartparkingplatform.dto.request.ParkingSpotUpdateRequest;
import com.rick.smartparkingplatform.dto.response.OccupancyResponse;
import com.rick.smartparkingplatform.dto.response.ParkingSpotResponse;
import com.rick.smartparkingplatform.entity.Parking;
import com.rick.smartparkingplatform.entity.ParkingSpot;
import com.rick.smartparkingplatform.enums.StatusParkingSpot;
import com.rick.smartparkingplatform.exception.ParkingSpotAlreadyExistsException;
import com.rick.smartparkingplatform.exception.ResourceNotFoundException;
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

    private final ParkingService parkingService;
    private final ParkingSpotRepository parkingSpotRepository;

    private ParkingSpot requestToEntity(ParkingSpotRequest request) {

        ParkingSpot parkingSpot = new ParkingSpot();
        Parking parking = parkingService.getCurrentParkingEntity();

        parkingSpot.setCode(request.code());
        parkingSpot.setSector(request.sector());
        parkingSpot.setFloor(request.floor());
        parkingSpot.setStatus(StatusParkingSpot.FREE);
        parkingSpot.setActive(true);
        parkingSpot.setCreatedAt(LocalDateTime.now());
        parkingSpot.setParking(parking);

        return parkingSpot;
    }

    private ParkingSpotResponse entityToResponse(ParkingSpot parkingSpot) {

        return new ParkingSpotResponse(
                parkingSpot.getId(),
                parkingSpot.getCode(),
                parkingSpot.getSector(),
                parkingSpot.getFloor(),
                parkingSpot.getStatus(),
                parkingSpot.isActive(),
                parkingSpot.getCreatedAt());
    }

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

        return specification;
    }

    private ParkingSpot findParkingSpotById(UUID id) {
        return parkingSpotRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Parking spot no found."));
    }


    private OccupancyResponse toOccupancy(Long totalSpots, Long availableSpots, Long occupiedSpots, BigDecimal occupancyRate) {
        return new OccupancyResponse(
                totalSpots, availableSpots, occupiedSpots, occupancyRate
        );
    }

    public Page<ParkingSpotResponse> findAll(Pageable pageable, ParkingSpotFilter filter) {

        Specification<ParkingSpot> specification = buildSpecification(filter);

        Page<ParkingSpot> parkingSpots = parkingSpotRepository.findAll(specification, pageable);

        return parkingSpots.map(this::entityToResponse);
    }

    public ParkingSpotResponse create(ParkingSpotRequest request) {

        ParkingSpot parkingSpot = requestToEntity(request);

        if (parkingSpotRepository.existsByCodeAndParking(parkingSpot.getCode(), parkingSpot.getParking())) {
            throw new ParkingSpotAlreadyExistsException();
        }
        ParkingSpot savedParkingSpot = parkingSpotRepository.save(parkingSpot);
        return entityToResponse(savedParkingSpot);
    }

    public ParkingSpotResponse update(ParkingSpotUpdateRequest request, UUID id) {

        ParkingSpot parkingSpot = findParkingSpotById(id);

        parkingSpot.setSector(request.sector());
        parkingSpot.setFloor(request.floor());

        return entityToResponse(parkingSpotRepository.save(parkingSpot));
    }

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
        BigDecimal occupancyRate = BigDecimal.valueOf(occupancy).setScale(2, RoundingMode.HALF_UP);

        return toOccupancy(totalSpots, availableSpots, occupiedSpots, occupancyRate);
    }

}

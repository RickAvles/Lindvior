package com.rick.smartparkingplatform.service;

import com.rick.smartparkingplatform.dto.request.ParkingRequest;
import com.rick.smartparkingplatform.dto.response.ParkingResponse;
import com.rick.smartparkingplatform.entity.Parking;
import com.rick.smartparkingplatform.exception.ParkingNotFoundException;
import com.rick.smartparkingplatform.exception.ResourceNotFoundException;
import com.rick.smartparkingplatform.repository.ParkingRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ParkingService {

    private final ParkingRepository parkingRepository;

    public ParkingService(ParkingRepository parkingRepository) {
        this.parkingRepository = parkingRepository;
    }

    private ParkingResponse entityToResponse(Parking parking) {
        
        return new ParkingResponse(
                parking.getId(),
                parking.getName(),
                parking.getAddress(),
                parking.getCreatedAt()
        );
    }

    public ParkingResponse getParking() {

        Parking parking = parkingRepository
                .findFirstByOrderByCreatedAtAsc()
                .orElseThrow(ParkingNotFoundException::new);

        return new ParkingResponse(
                parking.getId(),
                parking.getName(),
                parking.getAddress(),
                parking.getCreatedAt()
        );
    }

    public Parking getCurrentParkingEntity() {
        return parkingRepository.findFirstByOrderByCreatedAtAsc().orElseThrow(ParkingNotFoundException::new);
    }

    public ParkingResponse update(UUID id, ParkingRequest request) {

        Parking parking = parkingRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Parking with id " + id + " not found")
                );

        parking.setName(request.name());
        parking.setAddress(request.address());

        Parking updatedParking = parkingRepository.save(parking);

        return entityToResponse(updatedParking);
    }


}

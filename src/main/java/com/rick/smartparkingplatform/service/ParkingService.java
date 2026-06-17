package com.rick.smartparkingplatform.service;

import com.rick.smartparkingplatform.dto.request.ParkingRequest;
import com.rick.smartparkingplatform.dto.response.ParkingResponse;
import com.rick.smartparkingplatform.entity.Parking;
import com.rick.smartparkingplatform.repository.ParkingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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
                parking.getCapacity(),
                parking.getActive(),
                parking.getCreatedAt()
        );
    }

    public ParkingResponse create(ParkingRequest request) {

        Parking parking = new Parking();

        parking.setName(request.name());
        parking.setAddress(request.address());
        parking.setCapacity(request.capacity());
        parking.setCreatedAt(LocalDateTime.now());

        Parking savedParking = parkingRepository.save(parking);

        return entityToResponse(savedParking);
    }

    public List<ParkingResponse> findAll() {
        return parkingRepository.findAll()
                .stream()
                .map(this::entityToResponse)
                .toList();
    }

    public ParkingResponse findById(UUID id) {
        return parkingRepository.findById(id)
                .map(this::entityToResponse)
                .orElseThrow();
    }

    public ParkingResponse update(UUID id, ParkingRequest request) {
        Parking parking = parkingRepository
                .findById(id)
                .orElseThrow();

        parking.setName(request.name());
        parking.setAddress(request.address());
        parking.setCapacity(request.capacity());

        Parking updatedParking = parkingRepository.save(parking);

        return entityToResponse(updatedParking);
    }

    public void delete(UUID id) {
        Parking parking = parkingRepository.findById(id)
                .orElseThrow();

        parkingRepository.delete(parking);
    }

}

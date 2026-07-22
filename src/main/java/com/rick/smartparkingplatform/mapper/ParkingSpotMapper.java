package com.rick.smartparkingplatform.mapper;

import com.rick.smartparkingplatform.dto.request.ParkingSpotRequest;
import com.rick.smartparkingplatform.dto.response.ParkingSpotResponse;
import com.rick.smartparkingplatform.entity.ParkingSector;
import com.rick.smartparkingplatform.entity.ParkingSpot;
import com.rick.smartparkingplatform.enums.StatusParkingSpot;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ParkingSpotMapper {

    // Cria uma nova vaga.
    public ParkingSpot toEntity(
            ParkingSpotRequest request,
            ParkingSector parkingSector) {

        ParkingSpot parkingSpot = new ParkingSpot();

        parkingSpot.setCode(request.code());
        parkingSpot.setStatus(StatusParkingSpot.FREE);
        parkingSpot.setType(request.type());
        parkingSpot.setActive(true);
        parkingSpot.setCreatedAt(LocalDateTime.now());
        parkingSpot.setParkingSector(parkingSector);

        return parkingSpot;

    }

    // Converte uma entidade para o DTO de resposta.
    public ParkingSpotResponse toResponse(ParkingSpot parkingSpot) {

        ParkingSector parkingSector = parkingSpot.getParkingSector();

        return new ParkingSpotResponse(
                parkingSpot.getId(),
                parkingSpot.getCode(),
                parkingSector.getName(),
                parkingSector.getType(),
                parkingSpot.getType(),
                parkingSector.getFloor(),
                parkingSpot.getStatus(),
                parkingSpot.isActive(),
                parkingSpot.getCreatedAt()
        );

    }

}
package com.rick.smartparkingplatform.mapper;

import com.rick.smartparkingplatform.dto.request.ParkingSectorRequest;
import com.rick.smartparkingplatform.dto.response.ParkingSectorResponse;
import com.rick.smartparkingplatform.entity.Parking;
import com.rick.smartparkingplatform.entity.ParkingSector;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ParkingSectorMapper {

    // Cria um setor.
    public ParkingSector toEntity(
            ParkingSectorRequest request,
            Parking parking) {

        ParkingSector parkingSector = new ParkingSector();

        parkingSector.setName(request.name());
        parkingSector.setType(request.type());
        parkingSector.setFloor(request.floor());
        parkingSector.setActive(true);
        parkingSector.setCreatedAt(LocalDateTime.now());
        parkingSector.setParking(parking);

        return parkingSector;

    }

    // Atualiza um setor.
    public void updateEntity(
            ParkingSector parkingSector,
            ParkingSectorRequest request) {

        parkingSector.setName(request.name());
        parkingSector.setType(request.type());
        parkingSector.setFloor(request.floor());

    }

    // Converte uma entidade para o DTO de resposta.
    public ParkingSectorResponse toResponse(ParkingSector parkingSector) {

        return new ParkingSectorResponse(
                parkingSector.getId(),
                parkingSector.getName(),
                parkingSector.getType(),
                parkingSector.getFloor(),
                parkingSector.isActive(),
                parkingSector.getCreatedAt()
        );

    }

}
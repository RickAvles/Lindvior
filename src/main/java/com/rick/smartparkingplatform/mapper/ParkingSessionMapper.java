package com.rick.smartparkingplatform.mapper;

import com.rick.smartparkingplatform.dto.response.ParkingSessionResponse;
import com.rick.smartparkingplatform.entity.ParkingSession;
import com.rick.smartparkingplatform.entity.ParkingSpot;
import com.rick.smartparkingplatform.entity.Vehicle;
import com.rick.smartparkingplatform.enums.StatusParkingSession;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ParkingSessionMapper {

    // Cria uma nova sessão de estacionamento.
    public ParkingSession toEntity(
            Vehicle vehicle,
            ParkingSpot parkingSpot) {

        ParkingSession parkingSession = new ParkingSession();

        LocalDateTime now = LocalDateTime.now();

        parkingSession.setVehicle(vehicle);
        parkingSession.setParkingSpot(parkingSpot);
        parkingSession.setEntryTime(now);
        parkingSession.setCreatedAt(now);
        parkingSession.setStatus(StatusParkingSession.ENTERING);

        return parkingSession;

    }

    // Converte uma entidade para o DTO de resposta.
    public ParkingSessionResponse toResponse(ParkingSession parkingSession) {

        return new ParkingSessionResponse(
                parkingSession.getId(),
                parkingSession.getVehicle().getLicensePlate(),
                parkingSession.getVehicle().getType(),
                parkingSession.getEntryTime(),
                parkingSession.getExitTime(),
                parkingSession.getStatus(),
                parkingSession.getParkingSpot().getCode(),
                parkingSession.getCreatedAt()
        );

    }

}
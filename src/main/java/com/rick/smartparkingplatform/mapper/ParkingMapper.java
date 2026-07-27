package com.rick.smartparkingplatform.mapper;

import com.rick.smartparkingplatform.dto.request.ParkingRequest;
import com.rick.smartparkingplatform.dto.response.ParkingResponse;
import com.rick.smartparkingplatform.entity.Parking;
import org.springframework.stereotype.Component;

@Component
public class ParkingMapper {

    // Converte uma entidade para o DTO de resposta.
    public ParkingResponse toResponse(Parking parking) {

        return new ParkingResponse(
                parking.getId(),
                parking.getName(),
                parking.getAddress(),
                parking.getEntryGates(),
                parking.getExitGates(),
                parking.getEntryGateMinProcessingSeconds(),
                parking.getEntryGateMaxProcessingSeconds(),
                parking.getExitGateMinProcessingSeconds(),
                parking.getExitGateMaxProcessingSeconds(),
                parking.isActive(),
                parking.getCreatedAt(),
                parking.getOpeningTime(),
                parking.getClosingTime()
        );

    }

    // Atualiza a entidade com os dados da requisição.
    public void updateEntity(Parking parking, ParkingRequest request) {

        parking.setName(request.name());
        parking.setAddress(request.address());
        parking.setEntryGates(request.entryGates());
        parking.setExitGates(request.exitGates());
        parking.setEntryGateMinProcessingSeconds(request.entryGateMinProcessingSeconds());
        parking.setEntryGateMaxProcessingSeconds(request.entryGateMaxProcessingSeconds());
        parking.setExitGateMinProcessingSeconds(request.exitGateMinProcessingSeconds());
        parking.setExitGateMaxProcessingSeconds(request.exitGateMaxProcessingSeconds());
        parking.setActive(request.active());
        parking.setOpeningTime(request.openingTime());
        parking.setClosingTime(request.closingTime());

    }

}
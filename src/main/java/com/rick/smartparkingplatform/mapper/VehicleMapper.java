package com.rick.smartparkingplatform.mapper;

import com.rick.smartparkingplatform.dto.request.VehicleRequest;
import com.rick.smartparkingplatform.dto.response.VehicleResponse;
import com.rick.smartparkingplatform.entity.Vehicle;
import com.rick.smartparkingplatform.enums.VehicleType;
import com.rick.smartparkingplatform.simulation.parking.stay.StayProfile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class VehicleMapper {

    // Cria um veículo.
    public Vehicle toEntity(VehicleRequest request) {

        Vehicle vehicle = new Vehicle();

        vehicle.setLicensePlate(request.licensePlate());
        vehicle.setType(request.type());
        vehicle.setColor(request.color());
        vehicle.setActive(true);
        vehicle.setCreatedAt(LocalDateTime.now());

        return vehicle;

    }

    // Cria um veículo da simulação.
    public Vehicle toGeneratedEntity(
            String licensePlate,
            VehicleType type,
            String color,
            StayProfile stayProfile) {

        Vehicle vehicle = new Vehicle();

        vehicle.setLicensePlate(licensePlate);
        vehicle.setType(type);
        vehicle.setColor(color);
        vehicle.setStayProfile(stayProfile);
        vehicle.setActive(true);
        vehicle.setCreatedAt(LocalDateTime.now());

        return vehicle;

    }

    // Atualiza um veículo.
    public void updateEntity(
            Vehicle vehicle,
            VehicleRequest request) {

        vehicle.setLicensePlate(request.licensePlate());
        vehicle.setType(request.type());
        vehicle.setColor(request.color());

    }

    // Converte uma entidade para o DTO de resposta.
    public VehicleResponse toResponse(Vehicle vehicle) {

        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getLicensePlate(),
                vehicle.getType(),
                vehicle.getColor(),
                vehicle.isActive(),
                vehicle.getCreatedAt()
        );

    }

}
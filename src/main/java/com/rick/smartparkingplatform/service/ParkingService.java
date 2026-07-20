package com.rick.smartparkingplatform.service;

import com.rick.smartparkingplatform.dto.request.ParkingRequest;
import com.rick.smartparkingplatform.dto.response.ParkingResponse;
import com.rick.smartparkingplatform.entity.Parking;
import com.rick.smartparkingplatform.exception.InvalidParkingOperatingHoursException;
import com.rick.smartparkingplatform.exception.ParkingNotFoundException;
import com.rick.smartparkingplatform.exception.ResourceNotFoundException;
import com.rick.smartparkingplatform.repository.ParkingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParkingService {

    private final ParkingRepository parkingRepository;

    // =====================================================
    // API
    // =====================================================

    // Converte uma entidade Parking para o DTO de resposta.
    private ParkingResponse entityToResponse(Parking parking) {

        return new ParkingResponse(
                parking.getId(),
                parking.getName(),
                parking.getAddress(),
                parking.getCapacity(),
                parking.isActive(),
                parking.getCreatedAt(),
                parking.getOpeningTime(),
                parking.getClosingTime()
        );
    }

    // Valida se o horário de abertura é anterior ao horário de fechamento.
    private void validateOperatingHours(ParkingRequest request) {

        if (!request.openingTime().isBefore(request.closingTime())) {
            throw new InvalidParkingOperatingHoursException();
        }

    }

    // Retorna o estacionamento cadastrado.
    public ParkingResponse getParking() {

        Parking parking = parkingRepository.findFirstByOrderByCreatedAtAsc().orElseThrow(ParkingNotFoundException::new);

        return entityToResponse(parking);
    }

    // Atualiza os dados do estacionamento.
    public ParkingResponse update(UUID id, ParkingRequest request) {

        Parking parking = parkingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Parking with id " + id + " not found."));

        validateOperatingHours(request);

        parking.setName(request.name());
        parking.setAddress(request.address());
        parking.setCapacity(request.capacity());
        parking.setActive(request.active());
        parking.setOpeningTime(request.openingTime());
        parking.setClosingTime(request.closingTime());

        Parking updatedParking = parkingRepository.save(parking);

        return entityToResponse(updatedParking);
    }

    // =====================================================
    // SIMULAÇÃO
    // =====================================================

    // Retorna a entidade do estacionamento utilizada pela simulação.
    public Parking getCurrentParking() {

        return parkingRepository.findFirstByOrderByCreatedAtAsc().orElseThrow(ParkingNotFoundException::new);

    }

    // Verifica se o estacionamento já foi inicializado.
    public boolean exists() {

        return parkingRepository.existsBy();

    }

    // =====================================================
    // RELATÓRIOS
    // =====================================================

    // Nenhum méto do por enquanto.

}
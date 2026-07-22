package com.rick.smartparkingplatform.service;

import com.rick.smartparkingplatform.dto.request.ParkingRequest;
import com.rick.smartparkingplatform.dto.response.ParkingResponse;
import com.rick.smartparkingplatform.entity.Parking;
import com.rick.smartparkingplatform.exception.InvalidGateProcessingTimeException;
import com.rick.smartparkingplatform.exception.InvalidParkingOperatingHoursException;
import com.rick.smartparkingplatform.exception.ParkingNotFoundException;
import com.rick.smartparkingplatform.exception.ResourceNotFoundException;
import com.rick.smartparkingplatform.mapper.ParkingMapper;
import com.rick.smartparkingplatform.repository.ParkingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParkingService {

    private final ParkingRepository parkingRepository;
    private final ParkingMapper mapper;

    // =====================================================
    // API
    // =====================================================

    // Valida se o horário de abertura é anterior ao horário de fechamento.
    private void validateOperatingHours(ParkingRequest request) {

        if (!request.openingTime().isBefore(request.closingTime())) {
            throw new InvalidParkingOperatingHoursException();
        }

    }

    // Valida se o tempo mínimo de processamento das cancelas é menor que o máximo.
    private void validateGateProcessingTime(ParkingRequest request) {

        if (request.entryGateMinProcessingSeconds() > request.entryGateMaxProcessingSeconds()) {
            throw new InvalidGateProcessingTimeException();
        }

        if (request.exitGateMinProcessingSeconds() > request.exitGateMaxProcessingSeconds()) {
            throw new InvalidGateProcessingTimeException();
        }

    }

    // Retorna o estacionamento cadastrado.
    public ParkingResponse getParking() {

        Parking parking = parkingRepository.findFirstByOrderByCreatedAtAsc().orElseThrow(ParkingNotFoundException::new);

        return mapper.toResponse(parking);
    }

    // Atualiza os dados do estacionamento.
    public ParkingResponse update(UUID id, ParkingRequest request) {

        Parking parking = parkingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Parking with id " + id + " not found."));

        validateOperatingHours(request);
        validateGateProcessingTime(request);

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

        Parking updatedParking = parkingRepository.save(parking);

        return mapper.toResponse(updatedParking);
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
package com.rick.smartparkingplatform.service;

import com.rick.smartparkingplatform.dto.request.ParkingSectorRequest;
import com.rick.smartparkingplatform.dto.response.ParkingSectorResponse;
import com.rick.smartparkingplatform.entity.Parking;
import com.rick.smartparkingplatform.entity.ParkingSector;
import com.rick.smartparkingplatform.exception.ParkingNotFoundException;
import com.rick.smartparkingplatform.exception.ParkingSectorAlreadyExistsException;
import com.rick.smartparkingplatform.exception.ResourceNotFoundException;
import com.rick.smartparkingplatform.mapper.ParkingSectorMapper;
import com.rick.smartparkingplatform.repository.ParkingRepository;
import com.rick.smartparkingplatform.repository.ParkingSectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParkingSectorService {

    private final ParkingSectorRepository parkingSectorRepository;
    private final ParkingRepository parkingRepository;
    private final ParkingSectorMapper mapper;

    // =====================================================
    // API
    // =====================================================

    // Busca um setor pelo identificador.
    private ParkingSector findParkingSectorById(UUID id) {

        return parkingSectorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Parking sector not found."));
    }

    // Lista todos os setores cadastrados.
    public Page<ParkingSectorResponse> findAll(Pageable pageable) {

        return parkingSectorRepository.findAll(pageable).map(mapper::toResponse);
    }

    // Retorna um setor pelo identificador.
    public ParkingSectorResponse getById(UUID id) {

        ParkingSector parkingSector = findParkingSectorById(id);

        return mapper.toResponse(parkingSector);
    }

    // Cria um novo setor.
    public ParkingSectorResponse create(ParkingSectorRequest request) {

        Parking parking = parkingRepository
                .findFirstByOrderByCreatedAtAsc()
                .orElseThrow(ParkingNotFoundException::new);

        ParkingSector parkingSector =
                mapper.toEntity(request, parking);

        if (parkingSectorRepository.existsByNameAndParking(
                parkingSector.getName(),
                parkingSector.getParking())) {

            throw new ParkingSectorAlreadyExistsException();
        }

        ParkingSector savedParkingSector =
                parkingSectorRepository.save(parkingSector);

        return mapper.toResponse(savedParkingSector);
    }

    // Atualiza um setor.
    public ParkingSectorResponse update(UUID id, ParkingSectorRequest request) {

        ParkingSector parkingSector = findParkingSectorById(id);

        if (!parkingSector.getName().equals(request.name()) && parkingSectorRepository.existsByNameAndParking(request.name(), parkingSector.getParking())) {

            throw new ParkingSectorAlreadyExistsException();
        }

        parkingSector.setName(request.name());
        parkingSector.setType(request.type());
        parkingSector.setFloor(request.floor());

        ParkingSector updatedParkingSector = parkingSectorRepository.save(parkingSector);

        return mapper.toResponse(updatedParkingSector);
    }

    // Desativa um setor.
    public ParkingSectorResponse deactivate(UUID id) {

        ParkingSector parkingSector = findParkingSectorById(id);

        parkingSector.setActive(false);

        ParkingSector updatedParkingSector =
                parkingSectorRepository.save(parkingSector);

        return mapper.toResponse(updatedParkingSector);
    }

    // =====================================================
    // SIMULAÇÃO
    // =====================================================

    // Nenhum méto do por enquanto.

    // =====================================================
    // RELATÓRIOS
    // =====================================================

    // Nenhum méto do por enquanto.

}
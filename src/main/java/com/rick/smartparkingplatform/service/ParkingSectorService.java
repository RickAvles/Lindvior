package com.rick.smartparkingplatform.service;

import com.rick.smartparkingplatform.dto.request.ParkingSectorRequest;
import com.rick.smartparkingplatform.dto.response.ParkingSectorResponse;
import com.rick.smartparkingplatform.entity.Parking;
import com.rick.smartparkingplatform.entity.ParkingSector;
import com.rick.smartparkingplatform.exception.ParkingNotFoundException;
import com.rick.smartparkingplatform.exception.ParkingSectorAlreadyExistsException;
import com.rick.smartparkingplatform.exception.ResourceAlreadyExistsException;
import com.rick.smartparkingplatform.exception.ResourceNotFoundException;
import com.rick.smartparkingplatform.repository.ParkingRepository;
import com.rick.smartparkingplatform.repository.ParkingSectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParkingSectorService {

    private final ParkingSectorRepository parkingSectorRepository;
    private final ParkingRepository parkingRepository;

    /**
     * Converte o DTO de requisição para uma entidade ParkingSector.
     */
    private ParkingSector requestToEntity(ParkingSectorRequest request) {

        Parking parking = parkingRepository.findFirstByOrderByCreatedAtAsc()
                .orElseThrow(ParkingNotFoundException::new);

        ParkingSector parkingSector = new ParkingSector();

        parkingSector.setName(request.name());
        parkingSector.setType(request.type());
        parkingSector.setFloor(request.floor());
        parkingSector.setActive(true);
        parkingSector.setCreatedAt(LocalDateTime.now());
        parkingSector.setParking(parking);

        return parkingSector;
    }

    /**
     * Converte uma entidade ParkingSector para o DTO de resposta.
     */
    private ParkingSectorResponse entityToResponse(ParkingSector parkingSector) {

        return new ParkingSectorResponse(
                parkingSector.getId(),
                parkingSector.getName(),
                parkingSector.getType(),
                parkingSector.getFloor(),
                parkingSector.isActive(),
                parkingSector.getCreatedAt()
        );
    }

    /**
     * Busca um setor pelo identificador.
     */
    private ParkingSector findParkingSectorById(UUID id) {

        return parkingSectorRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Parking sector not found."));
    }

    /**
     * Lista todos os setores cadastrados.
     */
    public Page<ParkingSectorResponse> findAll(Pageable pageable) {

        return parkingSectorRepository.findAll(pageable)
                .map(this::entityToResponse);
    }

    /**
     * Busca um setor pelo identificador.
     */
    public ParkingSectorResponse getById(UUID id) {

        ParkingSector parkingSector = findParkingSectorById(id);

        return entityToResponse(parkingSector);
    }

    /**
     * Cria um novo setor.
     */
    public ParkingSectorResponse create(ParkingSectorRequest request) {

        ParkingSector parkingSector = requestToEntity(request);

        if (parkingSectorRepository.existsByNameAndParking(
                parkingSector.getName(),
                parkingSector.getParking())) {

            throw new ParkingSectorAlreadyExistsException();
        }

        ParkingSector savedParkingSector =
                parkingSectorRepository.save(parkingSector);

        return entityToResponse(savedParkingSector);
    }

    /**
     * Atualiza um setor.
     */
    public ParkingSectorResponse update(UUID id, ParkingSectorRequest request) {

        ParkingSector parkingSector = findParkingSectorById(id);

        if (!parkingSector.getName().equals(request.name())
                && parkingSectorRepository.existsByNameAndParking(
                request.name(),
                parkingSector.getParking())) {

            throw new ParkingSectorAlreadyExistsException();
        }

        parkingSector.setName(request.name());
        parkingSector.setType(request.type());
        parkingSector.setFloor(request.floor());

        ParkingSector updatedParkingSector =
                parkingSectorRepository.save(parkingSector);

        return entityToResponse(updatedParkingSector);
    }

    /**
     * Desativa um setor.
     */
    public ParkingSectorResponse deactivate(UUID id) {

        ParkingSector parkingSector = findParkingSectorById(id);

        parkingSector.setActive(false);

        ParkingSector updatedParkingSector =
                parkingSectorRepository.save(parkingSector);

        return entityToResponse(updatedParkingSector);
    }

}
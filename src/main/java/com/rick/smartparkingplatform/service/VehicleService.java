package com.rick.smartparkingplatform.service;

import com.rick.smartparkingplatform.dto.request.VehicleRequest;
import com.rick.smartparkingplatform.dto.response.VehicleResponse;
import com.rick.smartparkingplatform.entity.Vehicle;
import com.rick.smartparkingplatform.enums.VehicleType;
import com.rick.smartparkingplatform.exception.VehicleAlreadyExistsException;
import com.rick.smartparkingplatform.exception.VehicleNotFoundException;
import com.rick.smartparkingplatform.repository.VehicleRepository;
import com.rick.smartparkingplatform.simulation.enums.StayProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    /**
     * Converte o DTO de requisição para uma entidade Vehicle.
     */
    private Vehicle requestToEntity(VehicleRequest request) {

        Vehicle vehicle = new Vehicle();

        vehicle.setLicensePlate(request.licensePlate());
        vehicle.setType(request.type());
        vehicle.setColor(request.color());
        vehicle.setActive(true);
        vehicle.setCreatedAt(LocalDateTime.now());

        return vehicle;
    }

    /**
     * Converte uma entidade Vehicle para o DTO de resposta.
     */
    private VehicleResponse entityToResponse(Vehicle vehicle) {

        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getLicensePlate(),
                vehicle.getType(),
                vehicle.getColor(),
                vehicle.isActive(),
                vehicle.getCreatedAt()
        );
    }

    /**
     * Busca um veículo pelo identificador.
     */
    private Vehicle findVehicleById(UUID id) {

        return vehicleRepository.findById(id)
                .orElseThrow(VehicleNotFoundException::new);
    }

    /**
     * Lista todos os veículos cadastrados.
     */
    public Page<VehicleResponse> findAll(Pageable pageable) {

        return vehicleRepository.findAll(pageable)
                .map(this::entityToResponse);
    }

    /**
     * Busca um veículo pelo identificador.
     */
    public VehicleResponse getById(UUID id) {

        Vehicle vehicle = findVehicleById(id);

        return entityToResponse(vehicle);
    }

    /**
     * Cadastra um novo veículo.
     */
    public VehicleResponse create(VehicleRequest request) {

        if (vehicleRepository.existsByLicensePlate(request.licensePlate())) {
            throw new VehicleAlreadyExistsException();
        }

        Vehicle vehicle = requestToEntity(request);

        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        return entityToResponse(savedVehicle);
    }

    /**
     * Atualiza os dados de um veículo.
     */
    public VehicleResponse update(UUID id, VehicleRequest request) {

        Vehicle vehicle = findVehicleById(id);

        if (!vehicle.getLicensePlate().equals(request.licensePlate())
                && vehicleRepository.existsByLicensePlate(request.licensePlate())) {

            throw new VehicleAlreadyExistsException();
        }

        vehicle.setLicensePlate(request.licensePlate());
        vehicle.setType(request.type());
        vehicle.setColor(request.color());


        Vehicle updatedVehicle = vehicleRepository.save(vehicle);

        return entityToResponse(updatedVehicle);
    }

    /**
     * Desativa um veículo.
     */
    public VehicleResponse deactivate(UUID id) {

        Vehicle vehicle = findVehicleById(id);

        vehicle.setActive(false);

        Vehicle updatedVehicle = vehicleRepository.save(vehicle);

        return entityToResponse(updatedVehicle);
    }

    /**
     * Busca um veículo pela placa.
     */
    public Vehicle findByLicensePlate(String licensePlate) {

        return vehicleRepository.findByLicensePlate(licensePlate)
                .orElseThrow(VehicleNotFoundException::new);

    }

    /**
     * Retorna a quantidade de veículos cadastrados.
     */
    public long count() {

        return vehicleRepository.count();

    }

    /**
     * Retorna o veículo localizado na posição informada.
     */
    public Vehicle getVehicleAtPosition(int position) {

        Pageable pageable = PageRequest.of(position, 1);

        return vehicleRepository.findAll(pageable)
                .getContent()
                .getFirst();
    }

    /**
     * Cadastra um novo veículo gerado pela simulação.
     */
    public Vehicle createGeneratedVehicle(
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

        return vehicleRepository.save(vehicle);
    }

    /**
     * Verifica se já existe um veículo cadastrado com a placa informada.
     */
    public boolean existsByLicensePlate(String licensePlate) {

        return vehicleRepository.existsByLicensePlate(licensePlate);

    }

    /**
     * Retorna um veículo pela placa.
     *
     * @param licensePlate placa do veículo.
     * @return veículo encontrado.
     * @throws VehicleNotFoundException caso não exista.
     */
    public Vehicle getByLicensePlate(String licensePlate) {

        return vehicleRepository
                .findByLicensePlate(licensePlate)
                .orElseThrow(VehicleNotFoundException::new);
    }

}
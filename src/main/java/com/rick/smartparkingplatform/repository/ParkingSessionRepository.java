package com.rick.smartparkingplatform.repository;

import com.rick.smartparkingplatform.entity.ParkingSession;
import com.rick.smartparkingplatform.entity.Vehicle;
import com.rick.smartparkingplatform.enums.StatusParkingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ParkingSessionRepository extends JpaRepository<ParkingSession, UUID>, JpaSpecificationExecutor<ParkingSession> {

    boolean existsByVehicleAndStatus(Vehicle vehicle, StatusParkingSession status);

    List<ParkingSession> findAllByStatus(StatusParkingSession status);

    /**
     * Verifica se o veículo possui
     * uma sessão aberta.
     */
    boolean existsByVehicleIdAndStatus(UUID vehicleId, StatusParkingSession status);

    boolean existsByVehicleIdAndStatusNot(UUID vehicleId, StatusParkingSession status);

    List<ParkingSession> findByStatus(StatusParkingSession status);

}

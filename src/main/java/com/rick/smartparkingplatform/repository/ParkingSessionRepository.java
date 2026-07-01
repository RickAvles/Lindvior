package com.rick.smartparkingplatform.repository;

import com.rick.smartparkingplatform.entity.ParkingSession;
import com.rick.smartparkingplatform.enums.StatusParkingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ParkingSessionRepository extends JpaRepository<ParkingSession, UUID>, JpaSpecificationExecutor<ParkingSession> {

    boolean existsById(UUID id);

    boolean existsByLicensePlateAndStatus(String licensePlate, StatusParkingSession status);
}

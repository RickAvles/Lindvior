package com.rick.smartparkingplatform.repository;

import com.rick.smartparkingplatform.entity.Parking;
import com.rick.smartparkingplatform.entity.ParkingSpot;
import com.rick.smartparkingplatform.enums.StatusParkingSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, UUID>, JpaSpecificationExecutor<ParkingSpot> {

    boolean existsByCodeAndParking(String code, Parking parking);

    boolean existsByActiveTrue();

    Long countByActiveTrue();

    Long countByStatusAndActiveTrue(StatusParkingSpot statusParkingSpot);

    Optional<ParkingSpot> findFirstByStatusAndActiveTrue(StatusParkingSpot status);
}

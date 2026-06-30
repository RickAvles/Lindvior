package com.rick.smartparkingplatform.repository;

import com.rick.smartparkingplatform.entity.Parking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ParkingRepository extends JpaRepository<Parking, UUID> {

    boolean existsBy();

    Optional<Parking> findFirstByOrderByCreatedAtAsc();
}

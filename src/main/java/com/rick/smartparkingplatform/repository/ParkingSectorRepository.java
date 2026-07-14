package com.rick.smartparkingplatform.repository;

import com.rick.smartparkingplatform.entity.ParkingSector;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ParkingSectorRepository extends JpaRepository<ParkingSector, UUID> {

}
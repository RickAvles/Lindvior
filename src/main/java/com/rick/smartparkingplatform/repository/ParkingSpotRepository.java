package com.rick.smartparkingplatform.repository;

import com.rick.smartparkingplatform.entity.ParkingSector;
import com.rick.smartparkingplatform.entity.ParkingSpot;
import com.rick.smartparkingplatform.enums.StatusParkingSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, UUID>,
        JpaSpecificationExecutor<ParkingSpot> {

    // =====================================================
    // API
    // =====================================================

    boolean existsByCodeAndParkingSector(String code, ParkingSector parkingSector);

    // =====================================================
    // SIMULAÇÃO
    // =====================================================

    @Query("""
            SELECT ps
            FROM ParkingSpot ps
            WHERE ps.status = 'FREE'
              AND ps.active = true
            ORDER BY ps.createdAt
            LIMIT 1
            """)
    Optional<ParkingSpot> findNextAvailableSpot();

    // =====================================================
    // RELATÓRIOS
    // =====================================================

    Long countByActiveTrue();

    Long countByStatusAndActiveTrue(StatusParkingSpot status);

    boolean existsByStatusAndActiveTrue(StatusParkingSpot status);

}
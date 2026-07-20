package com.rick.smartparkingplatform.repository;

import com.rick.smartparkingplatform.entity.ParkingSession;
import com.rick.smartparkingplatform.enums.StatusParkingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ParkingSessionRepository extends JpaRepository<ParkingSession, UUID>,
        JpaSpecificationExecutor<ParkingSession> {

    // =====================================================
    // API
    // =====================================================


    // =====================================================
    // SIMULAÇÃO
    // =====================================================

    @Query("""
            SELECT COUNT(ps) > 0
            FROM ParkingSession ps
            WHERE ps.vehicle.id = :vehicleId
              AND ps.status <> 'FINISHED'
            """)
    boolean existsOpenSession(UUID vehicleId);

    List<ParkingSession> findByStatus(StatusParkingSession status);
}
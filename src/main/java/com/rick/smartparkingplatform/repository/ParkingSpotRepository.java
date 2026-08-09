package com.rick.smartparkingplatform.repository;

import com.rick.smartparkingplatform.entity.ParkingSector;
import com.rick.smartparkingplatform.entity.ParkingSpot;
import com.rick.smartparkingplatform.enums.ParkingSpotType;
import com.rick.smartparkingplatform.enums.StatusParkingSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, UUID>,
        JpaSpecificationExecutor<ParkingSpot> {

    // =====================================================
    // API
    // =====================================================

    boolean existsByCodeAndParkingSector(String code, ParkingSector parkingSector);

    List<ParkingSpot> findByActiveTrueOrderByParkingSectorNameAscCodeAsc();

    // =====================================================
    // SIMULAÇÃO
    // =====================================================

    // Retorna todas as vagas livres e ativas de um determinado tipo.
    List<ParkingSpot> findByStatusAndActiveTrueAndType(
            StatusParkingSpot status,
            ParkingSpotType type
    );

    // =====================================================
    // RELATÓRIOS
    // =====================================================

    Long countByActiveTrue();

    Long countByStatusAndActiveTrue(StatusParkingSpot status);

    boolean existsByStatusAndActiveTrue(StatusParkingSpot status);

}
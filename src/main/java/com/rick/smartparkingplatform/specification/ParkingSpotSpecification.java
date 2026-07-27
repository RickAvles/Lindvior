package com.rick.smartparkingplatform.specification;

import com.rick.smartparkingplatform.entity.ParkingSpot;
import com.rick.smartparkingplatform.enums.ParkingSectorType;
import com.rick.smartparkingplatform.enums.ParkingSpotType;
import com.rick.smartparkingplatform.enums.StatusParkingSpot;
import org.springframework.data.jpa.domain.Specification;

public class ParkingSpotSpecification {

    /**
     * Filtra vagas pelo nome do setor.
     */
    public static Specification<ParkingSpot> hasSector(String sector) {
        return (root, query, builder) ->
                builder.equal(root.join("parkingSector").get("name"), sector);
    }

    /**
     * Filtra vagas pelo piso do setor.
     */
    public static Specification<ParkingSpot> hasFloor(Integer floor) {
        return (root, query, builder) ->
                builder.equal(root.join("parkingSector").get("floor"), floor);
    }

    /**
     * Filtra vagas pelo status.
     */
    public static Specification<ParkingSpot> hasStatus(StatusParkingSpot status) {
        return (root, query, builder) ->
                builder.equal(root.get("status"), status);
    }

    /**
     * Filtra vagas pelo estado de ativação.
     */
    public static Specification<ParkingSpot> hasActive(Boolean active) {
        return (root, query, builder) ->
                builder.equal(root.get("active"), active);
    }

    /**
     * Filtra vagas pelo estado de ativação.
     */
    public static Specification<ParkingSpot> hasSpotType(ParkingSpotType type) {
        return (root, query, builder) ->
                builder.equal(root.get("type"), type);
    }

    /**
     * Filtra vagas pelo setor.
     */
    public static Specification<ParkingSpot> hasSectorType(ParkingSectorType type) {
        return (root, query, builder) ->
                builder.equal(root.join("parkingSector").get("type"), type);
    }

}
package com.rick.smartparkingplatform.specification;

import com.rick.smartparkingplatform.entity.ParkingSpot;
import com.rick.smartparkingplatform.enums.StatusParkingSpot;
import org.springframework.data.jpa.domain.Specification;

public class ParkingSpotSpecification {

    public static Specification<ParkingSpot> hasSector(String sector) {
        return (root, query, builder) -> builder.equal(root.get("sector"), sector);
    }

    public static Specification<ParkingSpot> hasFloor(Integer floor) {

        return (root, query, builder) -> builder.equal(root.get("floor"), floor);
    }

    public static Specification<ParkingSpot> hasStatus(StatusParkingSpot status) {
        return (root, query, builder) -> builder.equal(root.get("status"), status);
    }

    public static Specification<ParkingSpot> hasActive(Boolean active) {
        return (root, query, builder) -> builder.equal(root.get("active"), active);
    }
}


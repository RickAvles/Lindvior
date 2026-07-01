package com.rick.smartparkingplatform.specification;

import com.rick.smartparkingplatform.entity.ParkingSession;
import com.rick.smartparkingplatform.enums.StatusParkingSession;
import org.springframework.data.jpa.domain.Specification;

public class ParkingSessionSpecification {
    public static Specification<ParkingSession> hasLicensePlate(String licensePlate) {
        return (root, query, builder) -> builder.equal(root.get("licensePlate"), licensePlate);
    }

    public static Specification<ParkingSession> hasStatus(StatusParkingSession status) {
        return (root, query, builder) -> builder.equal(root.get("status"), status);
    }
}

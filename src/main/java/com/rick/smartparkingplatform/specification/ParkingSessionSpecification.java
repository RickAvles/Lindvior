package com.rick.smartparkingplatform.specification;

import com.rick.smartparkingplatform.entity.ParkingSession;
import com.rick.smartparkingplatform.enums.StatusParkingSession;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class ParkingSessionSpecification {
    public static Specification<ParkingSession> hasLicensePlate(String licensePlate) {
        return (root, query, builder) -> builder.equal(root.get("licensePlate"), licensePlate);
    }

    public static Specification<ParkingSession> hasStatus(StatusParkingSession status) {
        return (root, query, builder) -> builder.equal(root.get("status"), status);
    }

    public static Specification<ParkingSession> hasParkingSpotCode(String parkingSpotCode) {
        return (root, query, builder) -> builder.equal(root.get("parkingSpot").get("code"), parkingSpotCode);
    }

    public static Specification<ParkingSession> hasEntryTimeAfter(LocalDateTime startDate) {
        return (root, query, builder) -> builder.greaterThanOrEqualTo(root.get("entryTime"), startDate);
    }

    public static Specification<ParkingSession> hasEntryTimeBefore(LocalDateTime endDate) {
        return (root, query, builder) -> builder.lessThanOrEqualTo(root.get("exitTime"), endDate);
    }
}

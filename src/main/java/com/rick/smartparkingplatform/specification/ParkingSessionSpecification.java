package com.rick.smartparkingplatform.specification;

import com.rick.smartparkingplatform.entity.ParkingSession;
import com.rick.smartparkingplatform.enums.StatusParkingSession;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class ParkingSessionSpecification {

    /**
     * Filtra sessões pela placa do veículo.
     */
    public static Specification<ParkingSession> hasLicensePlate(String licensePlate) {
        return (root, query, builder) ->
                builder.equal(
                        root.join("vehicle").get("licensePlate"),
                        licensePlate
                );
    }

    /**
     * Filtra sessões pelo status.
     */
    public static Specification<ParkingSession> hasStatus(StatusParkingSession status) {
        return (root, query, builder) ->
                builder.equal(root.get("status"), status);
    }

    /**
     * Filtra sessões pelo código da vaga.
     */
    public static Specification<ParkingSession> hasParkingSpotCode(String parkingSpotCode) {
        return (root, query, builder) ->
                builder.equal(
                        root.join("parkingSpot").get("code"),
                        parkingSpotCode
                );
    }

    /**
     * Filtra sessões com entrada a partir da data informada.
     */
    public static Specification<ParkingSession> hasEntryTimeAfter(LocalDateTime startDate) {
        return (root, query, builder) ->
                builder.greaterThanOrEqualTo(root.get("entryTime"), startDate);
    }

    /**
     * Filtra sessões com saída até a data informada.
     */
    public static Specification<ParkingSession> hasExitTimeBefore(LocalDateTime endDate) {
        return (root, query, builder) ->
                builder.lessThanOrEqualTo(root.get("exitTime"), endDate);
    }

}
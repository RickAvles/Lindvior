package com.rick.smartparkingplatform.dto.dashboard;

import com.rick.smartparkingplatform.enums.ParkingSpotType;

import java.util.UUID;

public record DashboardSpot(

        UUID id,

        String code,

        ParkingSpotType type,

        boolean active,

        DashboardVehicle vehicle

) {
}
package com.rick.smartparkingplatform.dto.dashboard;

import com.rick.smartparkingplatform.enums.VehicleType;

import java.time.LocalDateTime;

public record DashboardVehicle(

        String licensePlate,
        VehicleType type,
        String color,
        LocalDateTime entryTime

) {
}
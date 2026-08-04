package com.rick.smartparkingplatform.simulation.dashboard.event;

import com.rick.smartparkingplatform.dto.dashboard.DashboardVehicle;

public record DashboardEvent(

        DashboardEventType type,

        DashboardVehicle vehicle,

        DashboardGateEvent gate,

        DashboardSpotEvent spot

) {
}
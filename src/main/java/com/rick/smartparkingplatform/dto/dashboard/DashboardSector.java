package com.rick.smartparkingplatform.dto.dashboard;

import java.util.List;
import java.util.UUID;

public record DashboardSector(

        UUID id,

        String name,

        double occupancyRate,

        List<DashboardSpot> spots

) {
}

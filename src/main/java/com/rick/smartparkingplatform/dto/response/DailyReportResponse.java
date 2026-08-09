package com.rick.smartparkingplatform.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record DailyReportResponse(
        LocalDate reportDate,
        LocalDateTime generatedAt,
        VehicleFlowReport vehicleFlow,
        OccupancyReport occupancy,
        List<SectorReport> sectors
) {
}
package com.rick.smartparkingplatform.dto.response;

public record SectorReport(
        String sectorName,
        String sectorType,
        Integer floor,
        long capacity,
        double averageOccupied,
        long maximumOccupied,
        double averageOccupancyRate,
        double maximumOccupancyRate
) {
}
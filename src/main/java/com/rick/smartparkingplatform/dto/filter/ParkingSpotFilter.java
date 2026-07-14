package com.rick.smartparkingplatform.dto.filter;

import com.rick.smartparkingplatform.enums.SectorType;
import com.rick.smartparkingplatform.enums.StatusParkingSpot;

public record ParkingSpotFilter(

        String sector,

        SectorType sectorType,

        Integer floor,

        StatusParkingSpot status,

        Boolean active

) {
}
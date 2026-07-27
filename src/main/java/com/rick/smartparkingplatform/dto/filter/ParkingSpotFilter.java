package com.rick.smartparkingplatform.dto.filter;

import com.rick.smartparkingplatform.enums.ParkingSectorType;
import com.rick.smartparkingplatform.enums.ParkingSpotType;
import com.rick.smartparkingplatform.enums.StatusParkingSpot;

public record ParkingSpotFilter(

        String sector,

        ParkingSpotType parkingSpotType,

        ParkingSectorType parkingSectorType,

        Integer floor,

        StatusParkingSpot status,

        Boolean active

) {
}
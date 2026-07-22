package com.rick.smartparkingplatform.dto.response;

public record GateResponse(

        String gate,
        boolean available,
        String vehiclePlate

) {
}
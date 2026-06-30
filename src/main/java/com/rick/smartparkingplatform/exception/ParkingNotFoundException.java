package com.rick.smartparkingplatform.exception;

public class ParkingNotFoundException extends BusinessException {
    public ParkingNotFoundException() {
        super("Parking not found.");
    }
}

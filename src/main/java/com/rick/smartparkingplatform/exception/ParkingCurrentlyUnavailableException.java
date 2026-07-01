package com.rick.smartparkingplatform.exception;

public class ParkingCurrentlyUnavailableException extends BusinessException {
    public ParkingCurrentlyUnavailableException() {
        super("Parking is currently unavailable.");
    }
}

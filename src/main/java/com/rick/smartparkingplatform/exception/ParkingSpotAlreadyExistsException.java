package com.rick.smartparkingplatform.exception;

public class ParkingSpotAlreadyExistsException extends BusinessException {
    public ParkingSpotAlreadyExistsException() {
        super("Parking spot with this code already exists.");
    }
}

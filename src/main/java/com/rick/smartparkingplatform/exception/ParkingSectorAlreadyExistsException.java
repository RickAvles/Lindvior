package com.rick.smartparkingplatform.exception;

public class ParkingSectorAlreadyExistsException extends BusinessException {
    public ParkingSectorAlreadyExistsException() {
        super("Parking sector already exists.");
    }
}

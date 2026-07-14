package com.rick.smartparkingplatform.exception;

public class VehicleAlreadyExistsException extends RuntimeException {
    public VehicleAlreadyExistsException() {
        super("Vehicle already exists.");
    }
}

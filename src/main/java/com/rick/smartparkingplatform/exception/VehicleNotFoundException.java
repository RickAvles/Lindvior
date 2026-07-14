package com.rick.smartparkingplatform.exception;

public class VehicleNotFoundException extends BusinessException {
    public VehicleNotFoundException() {
        super("Vehicle not found.");
    }
}

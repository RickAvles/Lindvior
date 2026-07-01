package com.rick.smartparkingplatform.exception;

public class ParkingAlreadyClosedException extends BusinessException {
    public ParkingAlreadyClosedException() {
        super("Parking session is already closed.");
    }
}

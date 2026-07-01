package com.rick.smartparkingplatform.exception;

public class OpenParkingSessionAlreadyExistsException extends BusinessException {
    public OpenParkingSessionAlreadyExistsException() {
        super("There is already an open parking session for this vehicle.");
    }
}

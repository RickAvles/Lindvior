package com.rick.smartparkingplatform.exception;

public class NoParkingSpotsAvailableException extends BusinessException {
    public NoParkingSpotsAvailableException() {
        super("No parking spots available.");
    }
}

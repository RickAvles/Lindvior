package com.rick.smartparkingplatform.exception;

public class ResourceAlreadyExistsException extends BusinessException {
    public ResourceAlreadyExistsException() {
        super("Parking sector already exists.");
    }
}

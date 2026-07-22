package com.rick.smartparkingplatform.exception;

public class InvalidGateProcessingTimeException extends BusinessException {

    public InvalidGateProcessingTimeException() {
        super("Gate minimum processing time cannot be greater than maximum processing time.");
    }

}
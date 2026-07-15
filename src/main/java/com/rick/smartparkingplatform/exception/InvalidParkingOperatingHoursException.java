package com.rick.smartparkingplatform.exception;

public class InvalidParkingOperatingHoursException extends BusinessException {
    public InvalidParkingOperatingHoursException() {
        super("The parking opening time must be earlier than the closing time.");
    }
}

package com.rick.smartparkingplatform.exception;

public class EmailAlreadyExistsException extends BusinessException {

    public EmailAlreadyExistsException() {
        super("Email already registered");
    }

}
package com.rick.smartparkingplatform.dto.response;

public record FieldValidationError(
        String field,
        String message
) {
}

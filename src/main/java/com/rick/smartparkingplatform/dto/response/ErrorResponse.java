package com.rick.smartparkingplatform.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
        Integer status,
        String code,
        String message,
        LocalDateTime timestamp,
        List<FieldValidationError> errors
) {
}

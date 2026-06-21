package com.rick.smartparkingplatform.exception;

import com.rick.smartparkingplatform.dto.response.ErrorResponse;
import com.rick.smartparkingplatform.dto.response.FieldValidationError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private FieldValidationError toValidationError(FieldError fieldError) {
        return new FieldValidationError(
                fieldError.getField(),
                fieldError.getDefaultMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handlerMethodArgumentNotValidException(MethodArgumentNotValidException exception) {

        List<FieldValidationError> listFieldvalidationError = exception.getBindingResult().getFieldErrors().stream().map(this::toValidationError).toList();

        ErrorResponse response = new ErrorResponse(
                exception.getStatusCode().value(),
                "VALIDATION_ERROR",
                "Request contains invalid fields",
                LocalDateTime.now(),
                listFieldvalidationError
        );


        return ResponseEntity.status(400).body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlerResourceNotFoundException(ResourceNotFoundException exception) {

        ErrorResponse response = new ErrorResponse(
                404,
                "RESOURCE_NOT_FOUND",
                exception.getMessage(),
                LocalDateTime.now(),
                List.of()
        );

        return ResponseEntity.status(404).body(response);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handlerBusinessException(BusinessException exception) {

        ErrorResponse response = new ErrorResponse(
                409,
                "BUSINESS_ERROR",
                exception.getMessage(),
                LocalDateTime.now(),
                List.of()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handlerBadCredentialsException(BadCredentialsException exception) {
        ErrorResponse response = new ErrorResponse(
                401,
                "AUTHENTICATION_ERROR",
                exception.getMessage(),
                LocalDateTime.now(),
                List.of()
        );
        return ResponseEntity.status(401).body(response);
    }
}

package com.payplux.exception.global;

import com.payplux.exception.ErrorResponse;
import com.payplux.exception.custom.DuplicateResourceException;
import com.payplux.exception.custom.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(
            UserNotFoundException ex,
            HttpServletRequest request) {

        return buildError(
                ex.getMessage(),
                HttpStatus.NOT_FOUND,
                "USER_NOT_FOUND",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(
            DuplicateResourceException ex,
            HttpServletRequest request) {

        return buildError(
                ex.getMessage(),
                HttpStatus.CONFLICT,
                "DUPLICATE_RESOURCE",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex,
            HttpServletRequest request) {

        return buildError(
                "Something went wrong",
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_ERROR",
                request.getRequestURI()
        );
    }

    private ResponseEntity<ErrorResponse> buildError(
            String message,
            HttpStatus httpStatus,
            String errorCode,
            String path) {

        ErrorResponse error = ErrorResponse.builder()
                .message(message)
                .statusCode(httpStatus.value())
                .errorCode(errorCode)
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();

        return new ResponseEntity<>(error, httpStatus);
    }
}
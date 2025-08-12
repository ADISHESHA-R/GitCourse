package com.quiz.question_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Catch-all for unhandled exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleAllExceptions(Exception ex, WebRequest request) {
        return buildErrorResponse(ex, "INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorDetails> handleNullPointerException(NullPointerException ex, WebRequest request) {
        return buildErrorResponse(ex, "NULL_POINTER_EXCEPTION", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDetails> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        return buildErrorResponse(ex, "ILLEGAL_ARGUMENT", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorDetails> handleNoSuchElementException(NoSuchElementException ex, WebRequest request) {
        return buildErrorResponse(ex, "RESOURCE_NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorDetails> handleTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        return buildErrorResponse(ex, "TYPE_MISMATCH", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .findFirst()
                .orElse(ex.getMessage());

        return buildErrorResponse(errorMessage, "VALIDATION_ERROR", HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ErrorDetails> buildErrorResponse(Exception ex, String type, HttpStatus status) {
        ErrorDetails error = new ErrorDetails(
                LocalDateTime.now(),
                type,
                status.value(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, status);
    }

    private ResponseEntity<ErrorDetails> buildErrorResponse(String message, String type, HttpStatus status) {
        ErrorDetails error = new ErrorDetails(
                LocalDateTime.now(),
                type,
                status.value(),
                message
        );
        return new ResponseEntity<>(error, status);
    }
}


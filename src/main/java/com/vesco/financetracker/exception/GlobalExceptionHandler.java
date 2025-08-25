package com.vesco.financetracker.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatus(ResponseStatusException ex) {
        HttpStatusCode status = ex.getStatusCode();
        String reason = ex.getReason();
        return ResponseEntity.status(status != null ? status.value() : HttpStatus.BAD_REQUEST.value())
                .body(reason != null ? reason : ex.getMessage());
    }

    @ExceptionHandler(ResourceForbiddenException.class)
    public ResponseEntity<String> handleForbidden(ResourceForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
}

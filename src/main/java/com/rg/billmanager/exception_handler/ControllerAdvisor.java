package com.rg.billmanager.exception_handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rg.billmanager.exception_handler.exception.InvalidCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class ControllerAdvisor {
    private static final Logger logger = LoggerFactory.getLogger(ControllerAdvisor.class);

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ErrorResponse> handleJsonProcessingException(JsonProcessingException ex) {
        logger.error("JSON processing error: {}", ex.getMessage(), ex);

        List<String> errors = List.of("Invalid JSON format: " + ex.getOriginalMessage());

        return new ResponseEntity<>(new ErrorResponse(LocalDateTime.now(), errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidCreationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOrderException(InvalidCreationException ex) {
        logger.warn("Invalid order request: {}", ex.getMessage(), ex);
        List<String> errors = List.of(ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(LocalDateTime.now(), errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({Exception.class, RuntimeException.class})
    public ResponseEntity<ErrorResponse> handleAllUnhandledExceptions(Exception ex) {
        logger.error("Unexpected server error: {}", ex.getMessage(), ex);
        List<String> errors = List.of(ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(LocalDateTime.now(), errors), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

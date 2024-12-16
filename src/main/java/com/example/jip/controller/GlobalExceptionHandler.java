package com.example.jip.controller;

import com.example.jip.services.ScheduleServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ScheduleServices.ScheduleValidationException.class)
    public ResponseEntity<String> handleScheduleValidationException(ScheduleServices.ScheduleValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}

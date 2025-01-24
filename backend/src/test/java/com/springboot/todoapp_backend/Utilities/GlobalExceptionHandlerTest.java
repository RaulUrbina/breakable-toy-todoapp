package com.springboot.todoapp_backend.Utilities;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandleValidationExceptions() {
        // Mock the exception
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("objectName", "fieldName", "must not be null");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        // Call the handler
        ResponseEntity<ApiResponse<Object>> response = handler.handleValidationExceptions(exception);

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("must not be null", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void testHandleHttpMessageNotReadable_Generic() {
        // Mock the exception without a specific cause
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Invalid request body");

        // Call the handler
        ResponseEntity<ApiResponse<Object>> response = handler.handleHttpMessageNotReadable(exception);

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid request body", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void testHandleDateTimeParseException() {
        // Mock the exception
        DateTimeParseException exception = new DateTimeParseException("Invalid date", "2023-01-32", 0);

        // Call the handler
        ResponseEntity<ApiResponse<Object>> response = handler.handleDateTimeParseException(exception);

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid date format: 2023-01-32. The correct format is 'yyyy-MM-dd'", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void testHandleIllegalArgumentException() {
        // Create the exception
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");

        // Call the handler
        ResponseEntity<ApiResponse<Object>> response = handler.handleIllegalArgumentException(exception);

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid argument", Objects.requireNonNull(response.getBody()).getMessage());
    }
}

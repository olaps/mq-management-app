package com.bank.mqmanagement.controller;

import com.bank.mqmanagement.exception.ErrorResponse;
import com.bank.mqmanagement.exception.GlobalExceptionHandler;
import com.bank.mqmanagement.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setRequestURI("/api/partners/1");
        webRequest = new ServletWebRequest(servletRequest);
    }

    @Test
    void testHandleResourceNotFoundException() {
        // Arrange
        ResourceNotFoundException ex = new ResourceNotFoundException("Resource not found");

        // Act
        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.handleResourceNotFoundException(ex, webRequest);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        ErrorResponse errorResponse = responseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getStatus());
        assertEquals("Resource not found", errorResponse.getMessage());
    }

    @Test
    void testHandleDataIntegrityViolationException() {
        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Data integrity violation");

        // Act
        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.handleDataIntegrityViolationException(ex, webRequest);

        // Assert
        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        ErrorResponse errorResponse = responseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.CONFLICT.value(), errorResponse.getStatus());
        assertEquals("Data integrity violation", errorResponse.getMessage());
    }
}
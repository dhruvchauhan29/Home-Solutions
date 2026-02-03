package com.homesolutions;

import com.homesolutions.dto.ErrorResponse;
import com.homesolutions.exception.BusinessException;
import com.homesolutions.exception.GlobalExceptionHandler;
import com.homesolutions.exception.ResourceNotFoundException;
import com.homesolutions.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void testResourceNotFoundException() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Resource not found");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFoundException(exception);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("RESOURCE_NOT_FOUND");
        assertThat(response.getBody().getMessage()).isEqualTo("Resource not found");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    void testBusinessException() {
        BusinessException exception = new BusinessException("Business rule violation");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBusinessException(exception);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("BUSINESS_ERROR");
        assertThat(response.getBody().getMessage()).isEqualTo("Business rule violation");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    void testUnauthorizedException() {
        UnauthorizedException exception = new UnauthorizedException("Unauthorized access");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUnauthorizedException(exception);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("UNAUTHORIZED");
        assertThat(response.getBody().getMessage()).isEqualTo("Unauthorized access");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    void testBadCredentialsException() {
        BadCredentialsException exception = new BadCredentialsException("Bad credentials");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBadCredentialsException(exception);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("INVALID_CREDENTIALS");
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid phone or password");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    void testAccessDeniedException() {
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAccessDeniedException(exception);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("FORBIDDEN");
        assertThat(response.getBody().getMessage()).isEqualTo("Access denied");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    void testValidationException() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("objectName", "fieldName", "Validation error");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(fieldError));

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(exception);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("VALIDATION_ERROR");
        assertThat(response.getBody().getMessage()).contains("Validation failed");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    void testGenericException() {
        Exception exception = new Exception("Unexpected error");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(exception);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("INTERNAL_SERVER_ERROR");
        assertThat(response.getBody().getMessage()).isEqualTo("An unexpected error occurred");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    void testResourceNotFoundException_WithDifferentMessage() {
        ResourceNotFoundException exception = new ResourceNotFoundException("User not found with ID: 123");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFoundException(exception);

        assertThat(response).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("User not found with ID: 123");
    }

    @Test
    void testBusinessException_WithDifferentMessage() {
        BusinessException exception = new BusinessException("Phone number already registered");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBusinessException(exception);

        assertThat(response).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Phone number already registered");
    }
}

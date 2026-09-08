package com.pointwest.prop.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
                        ResourceNotFoundException ex, HttpServletRequest request) {
                log.warn("Resource not found: {}", ex.getMessage());
                return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
        }

        @ExceptionHandler(ConflictException.class)
        public ResponseEntity<ErrorResponse> handleConflictException(
                        ConflictException ex, HttpServletRequest request) {
                log.warn("Conflict error: {}", ex.getMessage());
                return build(HttpStatus.CONFLICT, ex.getMessage(), request);
        }

        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<ErrorResponse> handleBadRequestException(
                        BadRequestException ex, HttpServletRequest request) {
                log.warn("Bad request: {}", ex.getMessage());
                return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ErrorResponse> handleAccessDeniedException(
                        AccessDeniedException ex, HttpServletRequest request) {
                log.warn("Access denied on {}: {}", request.getRequestURI(), ex.getMessage());
                return build(HttpStatus.FORBIDDEN, "You do not have permission to access this resource", request);
        }

        @ExceptionHandler(AuthenticationException.class)
        public ResponseEntity<ErrorResponse> handleAuthenticationException(
                        AuthenticationException ex, HttpServletRequest request) {
                log.warn("Authentication failed on {}: {}", request.getRequestURI(), ex.getMessage());
                return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGlobalException(
                        Exception ex, HttpServletRequest request) {
                log.error("Unhandled exception occurred: ", ex);
                return build(HttpStatus.INTERNAL_SERVER_ERROR,
                                "An unexpected error occurred. Please try again later.", request);
        }

        private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, HttpServletRequest request) {
                ErrorResponse errorResponse = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(status.value())
                                .error(status.getReasonPhrase())
                                .message(message)
                                .path(request.getRequestURI())
                                .build();
                return ResponseEntity.status(status).body(errorResponse);
        }
}
package com.pointwest.prop.common.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
                        ResourceNotFoundException ex, HttpServletRequest request) {
                log.warn("Resource not found on {}", request.getRequestURI());
                return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
        }

        @ExceptionHandler(EntityNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleEntityNotFoundException(
                        EntityNotFoundException ex, HttpServletRequest request) {
                log.warn("Resource not found on {}", request.getRequestURI());
                return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
        }

        @ExceptionHandler(ConflictException.class)
        public ResponseEntity<ErrorResponse> handleConflictException(
                        ConflictException ex, HttpServletRequest request) {
                log.warn("Conflict error on {}", request.getRequestURI());
                return build(HttpStatus.CONFLICT, ex.getMessage(), request);
        }

        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<ErrorResponse> handleBadRequestException(
                        BadRequestException ex, HttpServletRequest request) {
                log.warn("Bad request on {}", request.getRequestURI());
                return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
                        MethodArgumentNotValidException ex, HttpServletRequest request) {
                String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                                .findFirst()
                                .orElse("Validation failed");
                log.warn("Validation failure on {}: {}", request.getRequestURI(), errorMessage);
                return build(HttpStatus.BAD_REQUEST, errorMessage, request);
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ErrorResponse> handleAccessDeniedException(
                        AccessDeniedException ex, HttpServletRequest request) {
                log.warn("Access denied on {}", request.getRequestURI());
                return build(HttpStatus.FORBIDDEN, "You do not have permission to access this resource", request);
        }

        @ExceptionHandler(AccountLockedException.class)
        public ResponseEntity<ErrorResponse> handleAccountLockedException(
                        AccountLockedException ex, HttpServletRequest request) {
                log.warn("Login blocked due to account lockout on {}", request.getRequestURI());
                return build(HttpStatus.LOCKED, ex.getMessage(), request);
        }

        @ExceptionHandler(AuthenticationException.class)
        public ResponseEntity<ErrorResponse> handleAuthenticationException(
                        AuthenticationException ex, HttpServletRequest request) {
                log.warn("Authentication failed on {}", request.getRequestURI());
                return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
        }

        @ExceptionHandler(JwtException.class)
        public ResponseEntity<ErrorResponse> handleJwtException(
                        JwtException ex, HttpServletRequest request) {
                log.warn("Invalid JWT on {}", request.getRequestURI());
                return build(HttpStatus.UNAUTHORIZED, "Invalid or expired access token", request);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGlobalException(
                        Exception ex, HttpServletRequest request) {
                log.error("Unhandled exception on {}", request.getRequestURI(), ex);
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
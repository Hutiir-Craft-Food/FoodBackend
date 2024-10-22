package com.khutircraftubackend.auth.exception;

import com.khutircraftubackend.auth.exception.user.UnauthorizedException;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class UserExceptionHandler {

    /**
     * Handle unauthorized exception response entity.
     *
     * @param e the e
     * @return the response entity
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorizedException(UnauthorizedException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentialsException(
            BadCredentialsException ex, HttpServletRequest request) {

        Map<String, Object> errors = new LinkedHashMap<>();
        errors.put("timestamp", LocalDateTime.now());
        errors.put("status", 403);
        errors.put("reason", "Не правільні облікові дані");
        errors.put("message", ex.getMessage());
        errors.put("path", request.getRequestURI());

        return ResponseEntity.status(403).body(errors);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, Object> errors = new LinkedHashMap<>();
        Map<String, List<String>> errorsMessage = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        LinkedHashMap::new,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
                ));
            errors.put("timestamp", LocalDateTime.now());
            errors.put("status", ex.getStatusCode().value());
            errors.put("reason", HttpStatus.valueOf(ex.getStatusCode().value()).getReasonPhrase());
            errors.put("message", errorsMessage);
            errors.put("path", request.getRequestURI());

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @Hidden
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> getErrorAttributes(
            ResponseStatusException ex, HttpServletRequest request) {
        Map<String, Object> errors = new LinkedHashMap<>();
        errors.put("timestamp", LocalDateTime.now());
        errors.put("status", ex.getStatusCode().value());
        errors.put("reason", HttpStatus.valueOf(ex.getStatusCode().value()).getReasonPhrase());
        errors.put("message", ex.getReason());
        errors.put("path", request.getRequestURI());

        return ResponseEntity.status(ex.getStatusCode()).body(errors);
    }
}
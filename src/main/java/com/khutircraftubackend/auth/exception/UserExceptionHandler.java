package com.khutircraftubackend.auth.exception;

import com.khutircraftubackend.auth.exception.user.BadCredentialsException;
import com.khutircraftubackend.auth.exception.user.UnauthorizedException;
import com.khutircraftubackend.auth.exception.user.UserNotFoundException;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

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

    /**
     * Handles UserNotFoundException and returns a response with status 404 (Not Found).
     *
     * @param e the exception to handle
     * @return the response entity with the exception message and status 404
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Користувач не знайдений: " + e.getMessage());
    }

    /**
     * Handle bad credentials exception response entity.
     *
     * @param e the e
     * @return the response entity
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException e) {
        return new ResponseEntity<>("Неправильні облікові дані", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @Hidden
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatusException(ResponseStatusException ex) {
        String errorMessage = ex.getMessage();
        int startIndex = errorMessage.indexOf('"');
        int endIndex = errorMessage.lastIndexOf('"');
        if (startIndex != -1 && endIndex != -1 && startIndex != endIndex) {
            errorMessage = errorMessage.substring(startIndex + 1, endIndex);
        }

        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("ERROR TEST",  errorMessage);
        return ResponseEntity.status(ex.getStatusCode()).body(errorMap);
    }
}

package com.khutircraftubackend.auth.exception;

import com.khutircraftubackend.auth.exception.user.BadCredentialsException;
import com.khutircraftubackend.auth.exception.user.UnauthorizedException;
import com.khutircraftubackend.auth.exception.user.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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


}

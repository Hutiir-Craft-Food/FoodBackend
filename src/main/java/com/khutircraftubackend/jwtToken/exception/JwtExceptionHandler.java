package com.khutircraftubackend.jwtToken.exception;

import com.khutircraftubackend.jwtToken.exception.jwt.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class JwtExceptionHandler {
    @ExceptionHandler(InvalidJwtSignatureException.class)
    public ResponseEntity<String> handleInvalidJwtSignatureException(InvalidJwtSignatureException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles MalformedJwtTokenException and returns a response with status 400 (Bad Request).
     *
     * @param e the exception to handle
     * @return the response entity with the exception message and status 400
     */
    @ExceptionHandler(MalformedJwtTokenException.class)
    public ResponseEntity<String> handleMalformedJwtTokenException(MalformedJwtTokenException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles ExpiredJwtTokenException and returns a response with status 401 (Unauthorized).
     *
     * @param e the exception to handle
     * @return the response entity with the exception message and status 401
     */
    @ExceptionHandler(ExpiredJwtTokenException.class)
    public ResponseEntity<String> handleExpiredJwtTokenException(ExpiredJwtTokenException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles UnsupportedJwtTokenException and returns a response with status 400 (Bad Request).
     *
     * @param e the exception to handle
     * @return the response entity with the exception message and status 400
     */
    @ExceptionHandler(UnsupportedJwtTokenException.class)
    public ResponseEntity<String> handleUnsupportedJwtTokenException(UnsupportedJwtTokenException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles EmptyJwtClaimsException and returns a response with status 400 (Bad Request).
     *
     * @param e the exception to handle
     * @return the response entity with the exception message and status 400
     */
    @ExceptionHandler(EmptyJwtClaimsException.class)
    public ResponseEntity<String> handleEmptyJwtClaimsException(EmptyJwtClaimsException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles InvalidTokenException and returns a response with status 401 (Unauthorized).
     *
     * @param e the exception to handle
     * @return the response entity with the exception message and status 401
     */
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<String> handleInvalidTokenException(InvalidTokenException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }
}

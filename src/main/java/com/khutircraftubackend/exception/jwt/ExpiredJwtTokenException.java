package com.khutircraftubackend.exception.jwt;

/**
 * Exception thrown when the JWT token is expired.
 */

public class ExpiredJwtTokenException extends RuntimeException {
    public ExpiredJwtTokenException(String message) {
        super(message);
    }
}
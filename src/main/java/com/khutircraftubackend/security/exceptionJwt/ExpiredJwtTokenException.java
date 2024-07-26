package com.khutircraftubackend.security.exceptionJwt;

/**
 * Exception thrown when the JWT token is expired.
 */

public class ExpiredJwtTokenException extends RuntimeException {
    public ExpiredJwtTokenException(String message) {
        super(message);
    }
}

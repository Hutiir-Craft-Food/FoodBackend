package com.khutircraftubackend.security.exceptionJwt;

/**
 * Exception thrown when the JWT token format is invalid.
 */

public class MalformedJwtTokenException extends RuntimeException{
    public MalformedJwtTokenException(String message) {
        super(message);
    }
}

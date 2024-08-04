package com.khutircraftubackend.globalException.exception;

/**
 * Exception thrown when the JWT token is unsupported.
 */

public class UnsupportedJwtTokenException extends RuntimeException {
    public UnsupportedJwtTokenException(String message) {
        super(message);
    }
}

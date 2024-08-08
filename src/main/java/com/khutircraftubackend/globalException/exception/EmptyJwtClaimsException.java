package com.khutircraftubackend.globalException.exception;

/**
 * Exception thrown when the JWT claims string is empty.
 */

public class EmptyJwtClaimsException extends RuntimeException {
    public EmptyJwtClaimsException(String message) {
        super(message);
    }
}

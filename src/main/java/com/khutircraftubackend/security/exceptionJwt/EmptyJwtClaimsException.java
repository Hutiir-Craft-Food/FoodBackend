package com.khutircraftubackend.security.exceptionJwt;

/**
 * Exception thrown when the JWT claims string is empty.
 */

public class EmptyJwtClaimsException extends RuntimeException {
    public EmptyJwtClaimsException(String message) {
        super(message);
    }
}

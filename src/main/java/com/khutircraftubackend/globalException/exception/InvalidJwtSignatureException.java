package com.khutircraftubackend.globalException.exception;

/**
 * Exception thrown when the JWT signature is invalid.
 */

public class InvalidJwtSignatureException
    extends RuntimeException {
    public InvalidJwtSignatureException(String message) {
            super(message);
        }
    }


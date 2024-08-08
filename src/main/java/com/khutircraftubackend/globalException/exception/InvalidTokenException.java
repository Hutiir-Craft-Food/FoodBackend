package com.khutircraftubackend.globalException.exception;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String invalid_or_expired_token) {
        super(invalid_or_expired_token);
    }
    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}

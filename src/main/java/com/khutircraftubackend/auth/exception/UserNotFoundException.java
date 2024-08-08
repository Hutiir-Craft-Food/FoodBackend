package com.khutircraftubackend.auth.exception;

public class UserNotFoundException extends RuntimeException {
    // Конструктор без параметрів
    public UserNotFoundException() {
        super("User not found");
    }

    // Конструктор з повідомленням
    public UserNotFoundException(String message) {
        super(message);
    }

    // Конструктор з причиною
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    // Конструктор з причиною
    public UserNotFoundException(Throwable cause) {
        super(cause);
    }
}

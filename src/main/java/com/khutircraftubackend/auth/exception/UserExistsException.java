package com.khutircraftubackend.auth.exception;

public class UserExistsException extends RuntimeException {
    // Конструктор без параметрів
    public UserExistsException() {
        super("User not found");
    }

    // Конструктор з повідомленням
    public UserExistsException(String message) {
        super(message);
    }

    // Конструктор з причиною
    public UserExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    // Конструктор з причиною
    public UserExistsException(Throwable cause) {
        super(cause);
    }
}

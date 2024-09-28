package com.khutircraftubackend.auth.exception.user;

public class UserExistsException extends RuntimeException {

    // Конструктор з повідомленням
    public UserExistsException(String message) {
        super(message);
    }

}

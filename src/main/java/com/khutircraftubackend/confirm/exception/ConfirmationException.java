package com.khutircraftubackend.confirm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ConfirmationException extends RuntimeException {

    public ConfirmationException(String message) {
        super (message);
    }
}

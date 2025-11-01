package com.khutircraftubackend.storage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class StorageException extends RuntimeException {

    public StorageException(String message) {
        super(message);
    }
}

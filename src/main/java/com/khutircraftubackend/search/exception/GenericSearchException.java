package com.khutircraftubackend.search.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class GenericSearchException extends RuntimeException{
    public GenericSearchException(String message) {
        super(message);
    }
}

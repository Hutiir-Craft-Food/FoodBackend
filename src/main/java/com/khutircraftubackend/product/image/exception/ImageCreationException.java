package com.khutircraftubackend.product.image.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ImageCreationException extends RuntimeException {

    public ImageCreationException(String message) {
        super(message);
    }
}

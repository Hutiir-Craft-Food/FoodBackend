package com.khutircraftubackend.storage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class FileUploadException extends RuntimeException {
	public FileUploadException(String message) {
        super(message);
    }
}

package com.khutircraftubackend.storage.exception;

import com.khutircraftubackend.storage.exception.storage.FileNotFoundException;
import com.khutircraftubackend.storage.exception.storage.InvalidArgumentException;
import com.khutircraftubackend.storage.exception.storage.InvalidFileFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

@ControllerAdvice
@Slf4j
public class FileExceptionHandler {

    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleIOException(IOException e) {
        
        log.error("Failed to upload file: ", e);
        
        return e.getMessage();
    }
    
    @ExceptionHandler(FileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleFileNotFoundException(FileNotFoundException e) {
        
        log.error("File not found: ", e);
        
        return e.getMessage();
    }

    @ExceptionHandler(InvalidFileFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidFileFormatException(InvalidFileFormatException e) {
        
        log.error("Invalid file format: ", e);
        
        return e.getMessage();
    }
    
    @ExceptionHandler(InvalidArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleURISyntaxException(InvalidArgumentException e) {
        
        log.error("Invalid URL syntax: ", e);
        
        return e.getMessage();
    }
    
}

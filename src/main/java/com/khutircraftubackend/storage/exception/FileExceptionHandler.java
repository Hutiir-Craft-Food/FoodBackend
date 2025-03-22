package com.khutircraftubackend.storage.exception;

import com.khutircraftubackend.exception.GlobalErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
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
    public Object handleIOException(IOException ex, HttpServletRequest request) {
        log.error("Failed to upload file: ", ex);
        return GlobalErrorResponse.builder()
                .status(HttpStatus.LOCKED.value())
                .error(HttpStatus.LOCKED.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
    }
    
    @ExceptionHandler(FileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Object handleFileNotFoundException(FileNotFoundException ex, HttpServletRequest request) {
        log.error("File not found: ", ex);
        return GlobalErrorResponse.builder()
                .status(HttpStatus.LOCKED.value())
                .error(HttpStatus.LOCKED.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
    }
    
    @ExceptionHandler(InvalidFileFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleInvalidFileFormatException(InvalidFileFormatException ex, HttpServletRequest request) {
        log.error("Invalid file format: ", ex);
        return GlobalErrorResponse.builder()
                .status(HttpStatus.LOCKED.value())
                .error(HttpStatus.LOCKED.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
    }
    
    @ExceptionHandler(InvalidArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleURISyntaxException(InvalidArgumentException ex, HttpServletRequest request) {
        log.error("Invalid URL syntax: ", ex);
        return GlobalErrorResponse.builder()
                .status(HttpStatus.LOCKED.value())
                .error(HttpStatus.LOCKED.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
    }
}
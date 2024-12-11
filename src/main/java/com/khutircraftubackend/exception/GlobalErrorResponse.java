package com.khutircraftubackend.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Builder
public record GlobalErrorResponse(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSZ")
        Date timestamp,
        int status,
        String error,
        Object  message,
        String path
) {
    public GlobalErrorResponse {
        timestamp = new Date();
    }

    public GlobalErrorResponse(HttpStatus status, Object message, String path) {
        this(
                new Date(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path
        );
    }
}
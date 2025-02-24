package com.khutircraftubackend.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Builder
public record GlobalErrorResponse(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSX")
        Date timestamp,
        int status,
        String error,
        String  message,
        String path,

        @Nullable
        @JsonInclude(JsonInclude.Include.NON_NULL)
        Object data
) {
    public GlobalErrorResponse {
        timestamp = new Date();
    }

    public GlobalErrorResponse(HttpStatus status, String message, String path, Object data) {
        this(
                new Date(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                data
        );
    }
}
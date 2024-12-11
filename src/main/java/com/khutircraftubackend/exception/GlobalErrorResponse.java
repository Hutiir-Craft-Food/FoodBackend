package com.khutircraftubackend.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
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
}
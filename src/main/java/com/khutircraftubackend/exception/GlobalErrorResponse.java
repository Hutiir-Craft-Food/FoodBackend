package com.khutircraftubackend.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.Builder;

import java.util.Date;

@Builder
public record GlobalErrorResponse (
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd\'T\'HH:mm:ss.SSSX")
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
}
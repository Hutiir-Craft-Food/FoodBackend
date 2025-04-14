package com.khutircraftubackend.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.Builder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Builder
public record GlobalErrorResponse (
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd\'T\'HH:mm:ss.SSSXXX")
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

    public Map<String, Object> toMap() {
        Map<String, Object> map =  new LinkedHashMap<>();
        map.put("timestamp", new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss.SSSXXX").format(timestamp));
        map.put("status", status);
        map.put("error", error);
        map.put("message", message);
        map.put("path", path);

        if (data != null) {
            map.put("data", data);
        }

        return map;
    }
}
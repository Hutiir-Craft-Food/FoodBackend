package com.khutircraftubackend.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class GlobalErrorResponseWriter {

    private final ObjectMapper objectMapper;

    public void write(HttpServletRequest request, HttpServletResponse response, HttpStatus status,
                      String message, String path) {
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(status.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            GlobalErrorResponse errorResponse = GlobalErrorResponse.builder()
                    .status(status.value())
                    .error(status.getReasonPhrase())
                    .message(message)
                    .path(path)
                    .data(null)
                    .build();

            String json = objectMapper.writeValueAsString(errorResponse);
            response.getWriter().write(json);
        } catch (IOException e) {
            log.error("Ошибка при записи в response, статус: {}, contentType: {}, message: {}",
                    response.getStatus(), response.getContentType(), message, e);
        }
    }
}
package com.khutircraftubackend.jwt.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khutircraftubackend.exception.GlobalErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        log.error("Доступ запрещён: Host - {}, Ip - {}", request.getRemoteHost(), request.getRemoteAddr() );
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        ObjectMapper mapper = new ObjectMapper();
        GlobalErrorResponse errorResponse = new GlobalErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Unauthorized access",
                request.getRequestURI(),
                null
        );

        response.getWriter().write(mapper.writeValueAsString(errorResponse));
    }
}
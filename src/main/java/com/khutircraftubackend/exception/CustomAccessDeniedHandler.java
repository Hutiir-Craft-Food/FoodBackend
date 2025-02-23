//package com.khutircraftubackend.exception;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.web.access.AccessDeniedHandler;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
//@Slf4j
//@Component
//public class CustomAccessDeniedHandler implements AccessDeniedHandler {
//
//    @Override
//    public void handle(HttpServletRequest request, HttpServletResponse response,
//                       AccessDeniedException accessDeniedException) throws IOException {
//        log.error("AccessDeniedHandler: Host - {}, Ip - {}", request.getRemoteHost(), request.getRemoteAddr() );
//        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//
//        ObjectMapper mapper = new ObjectMapper();
//        GlobalErrorResponse errorResponse = new GlobalErrorResponse(
//                HttpStatus.FORBIDDEN,
//                "Access is denied. You do not have permission to access this resource.",
//                request.getRequestURI(),
//                null
//        );
//
//        response.getWriter().write(mapper.writeValueAsString(errorResponse));
//    }
//}

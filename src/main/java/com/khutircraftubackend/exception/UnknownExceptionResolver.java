package com.khutircraftubackend.exception;

import com.khutircraftubackend.auth.exception.UserBlockedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import static com.khutircraftubackend.auth.AuthResponseMessages.*;
import static org.springframework.http.HttpStatus.*;

@Component
public class UnknownExceptionResolver implements HandlerExceptionResolver {
    
    private static final MappingJackson2JsonView jsonView;
    private static final String ERROR_SERVER = "Тимчасова помилка сервера, зверніться до адміністрації сайту";
    
    static {
        jsonView = new MappingJackson2JsonView();
        jsonView.setExtractValueFromSingleKeyModel(true);
    }
    
    @Override
    public ModelAndView resolveException(@NonNull HttpServletRequest request,
                                         @NonNull HttpServletResponse response,
                                         Object handler,
                                         @NonNull Exception ex) {
        
        GlobalErrorResponse errorResponse;
        HttpStatus status;
        
        if (ex instanceof BadCredentialsException) {
            status = UNAUTHORIZED;
            errorResponse = GlobalErrorResponse.builder()
                    .status(status.value())
                    .error(status.getReasonPhrase())
                    .message(AUTH_INVALID_CREDENTIALS)
                    .path(request.getRequestURI())
                    .build();
            
        } else if (ex instanceof UserBlockedException) {
            status = FORBIDDEN;
            errorResponse = GlobalErrorResponse.builder()
                    .status(status.value())
                    .error(status.getReasonPhrase())
                    .message(AUTH_USER_BLOCKED)
                    .path(request.getRequestURI())
                    .build();
            
        } else {
            status = INTERNAL_SERVER_ERROR;
            errorResponse = GlobalErrorResponse.builder()
                    .status(status.value())
                    .error(status.getReasonPhrase())
                    .message(ERROR_SERVER)
                    .path(request.getRequestURI())
                    .build();
        }
        
        response.setStatus(status.value());
        ModelAndView mv = new ModelAndView(jsonView);
        mv.addObject("error", errorResponse);
        
        return mv;
    }
    
}
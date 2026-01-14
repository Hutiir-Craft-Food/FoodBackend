package com.khutircraftubackend.exception;

import com.khutircraftubackend.auth.exception.UserBlockedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import static com.khutircraftubackend.auth.AuthResponseMessages.INVALID_CREDENTIALS_PUBLIC;
import static com.khutircraftubackend.auth.AuthResponseMessages.USER_BLOCKED_PUBLIC;
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
        int status;
        
        if (ex instanceof BadCredentialsException) {
            status = HttpServletResponse.SC_UNAUTHORIZED;
            errorResponse = GlobalErrorResponse.builder()
                    .status(status)
                    .error(UNAUTHORIZED.getReasonPhrase())
                    .message(INVALID_CREDENTIALS_PUBLIC)
                    .path(request.getRequestURI())
                    .build();
            
        } else if (ex instanceof UserBlockedException) {
            status = HttpServletResponse.SC_FORBIDDEN;
            errorResponse = GlobalErrorResponse.builder()
                    .status(status)
                    .error(FORBIDDEN.getReasonPhrase())
                    .message(USER_BLOCKED_PUBLIC)
                    .path(request.getRequestURI())
                    .build();
            
        } else {
            status = INTERNAL_SERVER_ERROR.value();
            errorResponse = GlobalErrorResponse.builder()
                    .status(status)
                    .error(INTERNAL_SERVER_ERROR.getReasonPhrase())
                    .message(ERROR_SERVER)
                    .path(request.getRequestURI())
                    .build();
        }
        response.setStatus(status);
        ModelAndView mv = new ModelAndView(jsonView);
        mv.addObject("error", errorResponse);
        
        return mv;
    }
    
}
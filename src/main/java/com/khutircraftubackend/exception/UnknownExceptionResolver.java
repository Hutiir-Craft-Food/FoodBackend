package com.khutircraftubackend.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

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
        
        GlobalErrorResponse errorResponse = GlobalErrorResponse.builder()
                .status(INTERNAL_SERVER_ERROR.value())
                .error(INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message(ERROR_SERVER)
                .path(request.getRequestURI())
                .build();

        response.setStatus(INTERNAL_SERVER_ERROR.value());
        ModelAndView mv = new ModelAndView(jsonView);
        mv.addObject("error", errorResponse);
        
        return mv;
    }
    
}
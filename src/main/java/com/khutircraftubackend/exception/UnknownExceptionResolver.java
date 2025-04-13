package com.khutircraftubackend.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@Slf4j
@Component
public class UnknownExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Object handler, Exception ex) {

        log.error(ex.getMessage());

        GlobalErrorResponse errorResponse = GlobalErrorResponse.builder()
                .status(HttpStatus.I_AM_A_TEAPOT.value())
                .error(HttpStatus.I_AM_A_TEAPOT.getReasonPhrase())
                .message("Трясця! Щось дуже херове трапилось...")
                .path(request.getRequestURI())
                .build();

        ModelAndView mv = new ModelAndView();
        mv.setView(new MappingJackson2JsonView());
        mv.setStatus(HttpStatus.I_AM_A_TEAPOT);

        mv.addAllObjects(errorResponse.toMap());
        return mv;
    }
}

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

    private static final MappingJackson2JsonView jsonView;
    private static final HttpStatus STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

   static {
        jsonView = new MappingJackson2JsonView();
        jsonView.setExtractValueFromSingleKeyModel(true);
    }
    /**
     * Метод обробки непередбачених помилок. (Повертає 500 помилку)
     * Handles unexpected exceptions and returns standardized JSON error responses.
     * <p>
     * This resolver catches all exceptions not handled by other resolvers and
     * returns a 500 Internal Server Error response with details in JSON format.
     * </p>
     *
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler the executed handler, or null if none chosen
     * @param ex the exception that got thrown during handler execution
     * @return a ModelAndView with error details or null if the exception remains unresolved
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest request,
                HttpServletResponse response, Object handler, Exception ex) {

        log.error(ex.getMessage());

        GlobalErrorResponse errorResponse = GlobalErrorResponse.builder()
                .status(STATUS.value())
                .error(STATUS.getReasonPhrase())
                .message("Тимчасова помилка сервера, зверніться до адміністрації сайту")
                .path(request.getRequestURI())
                .build();

        ModelAndView mv = new ModelAndView(jsonView);
        mv.addObject("error", errorResponse);
        mv.setStatus(STATUS);

        return mv;
    }
}
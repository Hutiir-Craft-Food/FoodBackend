package com.khutircraftubackend.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Component
@Slf4j
public class UnknownExceptionResolver implements HandlerExceptionResolver {
    
    private static final MappingJackson2JsonView jsonView;
    private static final String ERROR_SERVER = "Тимчасова помилка сервера, зверніться до адміністрації сайту";
    private static final long ERROR_LOG_INTERVAL_NANOS = TimeUnit.SECONDS.toNanos(1);
    private final AtomicLong lastErrorLogNanos = new AtomicLong();
    private final AtomicLong suppressedErrorCount = new AtomicLong();
    
    static {
        jsonView = new MappingJackson2JsonView();
        jsonView.setExtractValueFromSingleKeyModel(true);
    }
    
    @Override
    public ModelAndView resolveException(@NonNull HttpServletRequest request,
                                         @NonNull HttpServletResponse response,
                                         Object handler,
                                         @NonNull Exception ex) {

        logUnhandledException(request, ex);
        
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

    private void logUnhandledException(HttpServletRequest request, Exception ex) {
        long now = System.nanoTime();
        long previous = lastErrorLogNanos.get();

        if ((previous == 0 || now - previous >= ERROR_LOG_INTERVAL_NANOS)
                && lastErrorLogNanos.compareAndSet(previous, now)) {
            long suppressed = suppressedErrorCount.getAndSet(0);
            if (suppressed == 0) {
                log.error("Unhandled exception for {}", request.getRequestURI(), ex);
            } else {
                log.error("Unhandled exception for {} ({} similar exceptions suppressed)",
                        request.getRequestURI(), suppressed, ex);
            }
        } else {
            suppressedErrorCount.incrementAndGet();
        }
    }
    
}
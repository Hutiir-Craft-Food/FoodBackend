package com.khutircraftubackend.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Метод обрабатывает входяшие запросы которые помечены в контроллере анатацией @Valid
     * @return - Возврашает коллекцию ошибок
     */
    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                     @NonNull HttpHeaders headers, HttpStatusCode status, @NonNull WebRequest request) {

        Map<String, List<String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        LinkedHashMap::new,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
                ));

        GlobalErrorResponse errorResponse = GlobalErrorResponse.builder()
                .status(status.value())
                .error(((HttpStatus) status).getReasonPhrase())
                .message("Validation error")
                .path(determineRequestPath(request))
                .data(errors)
                .build();
        return new ResponseEntity<>(errorResponse, headers, status);
    }

    /**
     * Метод обрабатывает не правильные ХТТП методы (на GET отправили POST)
     */
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                  @NonNull HttpHeaders headers, HttpStatusCode status, @NonNull WebRequest request) {
        GlobalErrorResponse errorResponse = GlobalErrorResponse.builder()
                .status(status.value())
                .error(((HttpStatus) status).getReasonPhrase())
                .message(ex.getMessage())
                .path(determineRequestPath(request))
                .build();
        return new ResponseEntity<>(errorResponse, headers, status);
    }

    /**
     * Метод обрабатывает 500 ошибки
     */
    @ExceptionHandler(InvalidDataAccessResourceUsageException.class)
    public ResponseEntity<Object> handleInternalServerError(InvalidDataAccessResourceUsageException ex, HttpServletRequest request ){

        log.error("Internal Server Error: {}", ex.getMessage());
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        GlobalErrorResponse errorResponse = GlobalErrorResponse.builder()
                .status(status.value())
                .error(( status).getReasonPhrase())
                .message("Тимчасова помилка сервера, зверніться до адміністрації сайту")
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(errorResponse, status);
    }

    private String determineRequestPath (WebRequest request){
        return Optional.ofNullable(((ServletWebRequest) request).getNativeRequest(HttpServletRequest.class))
                .map(HttpServletRequest::getRequestURI)
                .orElse("Unknown Path");
    }
}

package com.khutircraftubackend.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
    
    private static final String ERROR_BAD_REQUEST = "Bad Request";
    private static final String ERROR_VALIDATION = "Validation error";
    
    private ResponseEntity<Object> buildErrorResponse(
            HttpStatus status,
            String error,
            String message,
            String path,
            Object data) {
        
        GlobalErrorResponse errorResponse = GlobalErrorResponse.builder()
                .status(status.value())
                .error(error)
                .message(message)
                .path(path)
                .data(data)
                .build();
        
        return new ResponseEntity<>(errorResponse, status);
    }
    
    private String determineRequestPath(WebRequest request) {
        
        return Optional.ofNullable(((ServletWebRequest) request).getNativeRequest(HttpServletRequest.class))
                .map(HttpServletRequest::getRequestURI)
                .orElse("Unknown Path");
    }
    
    private String determineRequestPath(HttpServletRequest request) {
        
        return Optional.ofNullable(request.getRequestURI())
                .orElse("Unknown Path");
    }
    
    /**
     * Метод обрабатывает входяшие запросы которые помечены в контроллере анатацией @Valid
     *
     * @return - Возврашает коллекцию ошибок
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {
        
        Map<String, List<String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        LinkedHashMap::new,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
                ));
        
        return buildErrorResponse(
                (HttpStatus) status,
                ERROR_BAD_REQUEST,
                ERROR_VALIDATION,
                determineRequestPath(request),
                errors);
    }
    
    /**
     * Метод обрабатывает не правильные ХТТП методы (на GET отправили POST)
     */
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {
        
        return buildErrorResponse(
                (HttpStatus) status,
                "Method Not Allowed",
                ex.getMessage(),
                determineRequestPath(request),
                null);
    }
    
    // Відсутній параметр запиту query
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {
    
        String message = "Параметр '" + ex.getParameterName() +
                "' є обовʼязковим";
        
        return buildErrorResponse(
                (HttpStatus) status,
                ERROR_BAD_REQUEST,
                message,
                determineRequestPath(request),
                null);
    }
    
    // Порожній query
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(
            ConstraintViolationException ex,
            HttpServletRequest request) {
        
        Map<String, List<String>> errors = ex.getConstraintViolations()
                .stream()
                .collect(Collectors.groupingBy(
                        violation -> violation.getPropertyPath().toString(),
                        LinkedHashMap::new,
                        Collectors.mapping(ConstraintViolation::getMessage, Collectors.toList())
                ));
        
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ERROR_BAD_REQUEST,
                ERROR_VALIDATION,
                determineRequestPath(request),
                errors);
    }
}

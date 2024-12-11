package com.khutircraftubackend.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
                                     HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        Map<String, List<String>> errorsMessage = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        LinkedHashMap::new,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
                ));

        GlobalErrorResponse build = GlobalErrorResponse.builder()
                .status(status.value())
                .error(HttpStatus.valueOf(status.value()).getReasonPhrase())
                .message("validation error")
                .path(request.getDescription(false).replace("uri=", ""))
                .data(errorsMessage)
                .build();
        return new ResponseEntity<>(build, headers, status);
    }

    /**
     * Метод обрабатывает не правильные ХТТП методы (на GET отправили POST)
     */
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                      HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        GlobalErrorResponse build = GlobalErrorResponse.builder()
                .status(status.value())
                .error(HttpStatus.valueOf(status.value()).getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(build, headers, status);
    }
}

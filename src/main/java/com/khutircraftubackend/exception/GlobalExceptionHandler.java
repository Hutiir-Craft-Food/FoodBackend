package com.khutircraftubackend.exception;

import com.khutircraftubackend.exception.jwt.*;
import com.khutircraftubackend.exception.user.BadCredentialsException;
import com.khutircraftubackend.exception.user.UnauthorizedException;
import com.khutircraftubackend.exception.user.UserNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.nio.file.AccessDeniedException;

/**
 * Global exception handler for handling JWT validation exceptions.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {


    /**
     * Handle unauthorized exception response entity.
     *
     * @param e the e
     * @return the response entity
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorizedException(UnauthorizedException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handle bad credentials exception response entity.
     *
     * @param e the e
     * @return the response entity
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException e) {
        return new ResponseEntity<>("Неправильні облікові дані", HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles InvalidJwtSignatureException and returns a response with status 401 (Unauthorized).
     *
     * @param e the exception to handle
     * @return the response entity with the exception message and status 401
     */
    @ExceptionHandler(InvalidJwtSignatureException.class)
    public ResponseEntity<String> handleInvalidJwtSignatureException(InvalidJwtSignatureException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles MalformedJwtTokenException and returns a response with status 400 (Bad Request).
     *
     * @param e the exception to handle
     * @return the response entity with the exception message and status 400
     */
    @ExceptionHandler(MalformedJwtTokenException.class)
    public ResponseEntity<String> handleMalformedJwtTokenException(MalformedJwtTokenException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles ExpiredJwtTokenException and returns a response with status 401 (Unauthorized).
     *
     * @param e the exception to handle
     * @return the response entity with the exception message and status 401
     */
    @ExceptionHandler(ExpiredJwtTokenException.class)
    public ResponseEntity<String> handleExpiredJwtTokenException(ExpiredJwtTokenException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles UnsupportedJwtTokenException and returns a response with status 400 (Bad Request).
     *
     * @param e the exception to handle
     * @return the response entity with the exception message and status 400
     */
    @ExceptionHandler(UnsupportedJwtTokenException.class)
    public ResponseEntity<String> handleUnsupportedJwtTokenException(UnsupportedJwtTokenException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles EmptyJwtClaimsException and returns a response with status 400 (Bad Request).
     *
     * @param e the exception to handle
     * @return the response entity with the exception message and status 400
     */
    @ExceptionHandler(EmptyJwtClaimsException.class)
    public ResponseEntity<String> handleEmptyJwtClaimsException(EmptyJwtClaimsException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles InvalidTokenException and returns a response with status 401 (Unauthorized).
     *
     * @param e the exception to handle
     * @return the response entity with the exception message and status 401
     */
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<String> handleInvalidTokenException(InvalidTokenException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles UserNotFoundException and returns a response with status 404 (Not Found).
     *
     * @param e the exception to handle
     * @return the response entity with the exception message and status 404
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Handler runtime exception response entity.
     *
     * @param ex      the ex
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(RuntimeException.class)
//виняток який дозволяє уникнути використання try-catch блоків у контролерах
    public ResponseEntity<?> handlerRuntimeException(RuntimeException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    /**
     * Handle null pointer exception response entity.
     *
     * @param ex      the ex
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(NullPointerException.class)//виняток при зверненні до методу через null
    public ResponseEntity<?> handleNullPointerException(NullPointerException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Виникла помилка: NullPointerException");
    }

    /**
     * Handle entity not found exception response entity.
     *
     * @param ex      the ex
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(EntityNotFoundException.class)//виняток коли відсутня сутність у БД
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Сутність не знайдена: " + ex.getMessage());
    }

    /**
     * Handle constraint violation exception response entity.
     *
     * @param ex      the ex
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(ConstraintViolationException.class)//виняток по обмеженню БД
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Порушено обмеження: " + ex.getMessage());
    }

    /**
     * Handle method argument not valid exception response entity.
     *
     * @param ex      the ex
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)//виняток по валідації аргументів методу
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        StringBuilder errors = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("\n");
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.toString());
    }


    /**
     * Handle access denied exception response entity.
     *
     * @param ex      the ex
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(AccessDeniedException.class)//виняток по правам доступу у користувачів
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Доступ заборонено: " + ex.getMessage());
    }

    /**
     * Handle illegal argument exception response entity.
     *
     * @param ex the ex
     * @return the response entity
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Глобальне повідомлення: " + ex.getMessage());
    }

    /**
     * Handle generic exception response entity.
     *
     * @param ex the ex
     * @return the response entity
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Глобальне повідомлення: Внутрішня помилка сервера. Будь ласка, спробуйте пізніше.");
    }

}
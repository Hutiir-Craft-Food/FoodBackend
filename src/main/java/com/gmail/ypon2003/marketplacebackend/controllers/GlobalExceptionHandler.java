package com.gmail.ypon2003.marketplacebackend.controllers;

import jakarta.persistence.EntityNotFoundException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.nio.file.AccessDeniedException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
//виняток який дозволяє уникнути використання try-catch блоків у контролерах
    public ResponseEntity<?> handlerRuntimeException(RuntimeException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)//виняток при зверненні до методу через null
    public ResponseEntity<?> handleNullPointerException(NullPointerException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Виникла помилка: NullPointerException");
    }

    @ExceptionHandler(EntityNotFoundException.class)//виняток коли відсутня сутність у БД
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Сутність не знайдена: " + ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)//виняток по обмеженню БД
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Порушено обмеження: " + ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)//виняток по валідації аргументів методу
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        StringBuilder errors = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("\n");
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.toString());
    }


    @ExceptionHandler(AccessDeniedException.class)//виняток по правам доступу у користувачів
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Доступ заборонено: " + ex.getMessage());
    }

}

package com.khutircraftubackend.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Клас LoginRequest використовується для передачі даних запиту на вхід.
 */
public record LoginRequest (
    
    @Email(message = "Неправильний формат e-mail")
    @NotBlank(message = "E-mail не може бути порожнім")
    String email,

    @NotBlank(message = "Пароль не може бути порожнім")
    String password)
{
}

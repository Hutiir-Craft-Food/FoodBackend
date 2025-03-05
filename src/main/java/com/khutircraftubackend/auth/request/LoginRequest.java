package com.khutircraftubackend.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Клас LoginRequest використовується для передачі даних запиту на вхід.
 */
public record LoginRequest (
    @NotBlank(message = "E-mail не може бути порожнім")
    @Pattern(regexp = "^[A-Za-z\\d+_.-]+@[A-Za-z\\d.-]+\\.[A-Za-z]{2,}$")
    String email,

    @NotBlank(message = "Пароль не може бути порожнім")
    @Size(min = 8, max = 30, message = "Пароль має містити від 8 до 30 символів")
    @Pattern(regexp = "^[^!@#$%^&*()_+=]*$", message = "Пароль не може містити спеціальні символи !@#$%^&*()_+=")
    String password)
{
}

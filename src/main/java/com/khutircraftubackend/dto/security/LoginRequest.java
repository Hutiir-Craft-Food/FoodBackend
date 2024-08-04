package com.khutircraftubackend.dto.security;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Клас LoginRequest використовується для передачі даних запиту на вхід.
 */

@Getter
@Setter
@RequiredArgsConstructor
public class LoginRequest {
    @NotBlank(message = "E-mail не може бути порожнім")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    String email;

    @NotBlank(message = "Пароль не може бути порожнім")
    @Size(min = 8, max = 30, message = "Пароль має містити від 8 до 30 символів")
    @Pattern(regexp = "^[^!@#$%^&*()_+=]*$", message = "Пароль не може містити спеціальні символи !@#$%^&*()_+=")
    String password;
}

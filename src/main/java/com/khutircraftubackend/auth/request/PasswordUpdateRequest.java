package com.khutircraftubackend.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * Клас PasswordUpdateRequest використовується для передачі даних запиту на оновлення пароля.
 */

@Builder
public record PasswordUpdateRequest (

    @NotBlank(message = "Пароль не може бути порожнім")
    @Size(min = 8, max = 30, message = "Пароль має містити від 8 до 30 символів")
    @Pattern(regexp = "^[^!@#$%^&*()_+=]*$", message = "Пароль не може містити спеціальні символи !@#$%^&*()_+=")
    String password)
{
}

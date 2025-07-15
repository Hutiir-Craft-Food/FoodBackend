package com.khutircraftubackend.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

/**
 * Клас PasswordUpdateRequest використовується для передачі даних запиту на оновлення пароля.
 */

@Builder
public record PasswordUpdateRequest (
        
        @NotBlank(message = "Пароль не може бути порожнім")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=])[ A-Za-z\\d!@#$%^&*()_+\\-=]{8,30}$",
                message = "Пароль має містити 8–30 символів, великі й малі латинські літери, цифри та спецсимволи і може містити пробіли"
        )
        String password)
{
}

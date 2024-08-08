package com.khutircraftubackend.auth.security;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

/**
 * Клас PasswordRecoveryRequest використовується для передачі даних запиту на відновлення пароля.
 */

@Builder
public record PasswordRecoveryRequest (
    @NotBlank(message = "E-mail не може бути порожнім")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    String email)
{
}

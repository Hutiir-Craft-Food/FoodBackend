package com.khutircraftubackend.dto.security;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Клас PasswordRecoveryRequest використовується для передачі даних запиту на відновлення пароля.
 */

@Getter
@Setter
@RequiredArgsConstructor
public class PasswordRecoveryRequest {
    @NotBlank(message = "E-mail не може бути порожнім")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    String email;
}

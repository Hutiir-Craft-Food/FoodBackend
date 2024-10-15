package com.khutircraftubackend.userprofile.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ConfirmEmailRequest (

        @NotBlank(message = "E-mail не може бути порожнім")
        @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        String newEmail,

        @Pattern(regexp = "\\d{6}", message = "Токен должен содержать только 6 цифры")
        String confirmToken
) {
}

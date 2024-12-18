package com.khutircraftubackend.confirm;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ConfirmRequest(

        @NotBlank(message = "Токен не може бути порожнім.")
        @Pattern(regexp = "\\d{6}", message = "Токен має містити лише 6 цифр.")
        String confirmationToken
) {
}

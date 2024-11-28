package com.khutircraftubackend.confirm;

import jakarta.validation.constraints.Pattern;

public record ConfirmRequest(

        @Pattern(regexp = "\\d{6}", message = "Токен должен содержать только 6 цифры")
        String confirmationToken
) {
}

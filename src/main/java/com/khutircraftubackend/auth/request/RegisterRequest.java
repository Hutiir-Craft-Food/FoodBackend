package com.khutircraftubackend.auth.request;

import com.khutircraftubackend.auth.Role;
import com.khutircraftubackend.seller.SellerDTO;
import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotBlank(message = "E-mail не може бути порожнім")
        @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
        String email,

        @NotBlank(message = "Пароль не може бути порожнім")
        @Size(min = 8, max = 30, message = "Пароль має містити від 8 до 30 символів")
        @Pattern(regexp = "^[^!@#$%^&*()_+=]*$", message = "Пароль не може містити спеціальні символи !@#$%^&*()_+=")
        String password,

        @NotNull(message = "Роль не може бути порожньою")
        Role role,

        SellerDTO details
) {
}

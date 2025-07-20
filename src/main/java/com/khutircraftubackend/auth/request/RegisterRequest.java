package com.khutircraftubackend.auth.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.khutircraftubackend.seller.SellerDTO;
import com.khutircraftubackend.user.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
        
        @Email(message = "Неправильний формат e-mail")
        @NotBlank(message = "E-mail не може бути порожнім")
        String email,

        @NotBlank(message = "Пароль не може бути порожнім")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=])[ A-Za-z\\d!@#$%^&*()_+\\-=]{8,30}$",
                message = "Пароль має містити 8–30 символів, великі й малі латинські літери, цифри та спецсимволи і може містити пробіли"
        )
        String password,

        @NotNull(message = "Роль не може бути порожньою.")
        Role role,

        @JsonProperty("marketingConsent")
        @NotNull (message = "Розсилка не може бути порожньою.")
        Boolean isReceiveAdvertising,

        @Valid ()
        SellerDTO details
) {
}
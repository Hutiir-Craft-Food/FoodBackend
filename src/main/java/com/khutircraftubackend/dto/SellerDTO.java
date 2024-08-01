package com.khutircraftubackend.dto;

import com.khutircraftubackend.validation.annotation.PasswordMatches;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@PasswordMatches
public record SellerDTO(
        @NotBlank(message = "Назва компанії не може бути порожнім")
        @Size(max = 200, message = "Назва компанії не може перевищувати 200 символів")
        @Pattern(regexp = "^[a-zA-Zа-яА-ЯіїєґІЇЄҐ\\s]*$", message = "Назва компанії має містити лише українські або англійські літери")
        String name,

        @NotBlank(message = "E-mail не може бути порожнім")
        @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
        String email,

        @NotBlank(message = "Номер телефону не може бути порожнім")
        @Pattern(regexp = "^\\+380\\d{9}$", message = "Номер телефону має бути у форматі +380__________")
        String phoneNumber,

        @NotBlank(message = "Пароль не може бути порожнім")
        @Size(min = 8, max = 30, message = "Пароль має містити від 8 до 30 символів")
        @Pattern(regexp = "^[^!@#$%^&*()_+=]*$", message = "Пароль не може містити спеціальні символи !@#$%^&*()_+=")
        String password,

        @NotBlank(message = "Підтвердження паролю не може бути порожнім")
        @Size(min = 8, max = 30, message = "Підтвердження паролю має містити від 8 до 30 символів")
        @Pattern(regexp = "^[^!@#$%^&*()_+=]*$", message = "Підтвердження паролю не може містити спеціальні символи !@#$%^&*()_+=")
        String confirmPassword,

        String confirmationCode,

        boolean enabled
) {
    //метод перевірки відповідності пароля і підтвердження
    public boolean isPasswordMatching() {
        return this.password.equals(this.confirmPassword);
    }
}

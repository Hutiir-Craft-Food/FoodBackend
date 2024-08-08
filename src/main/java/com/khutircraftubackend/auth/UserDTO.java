package com.khutircraftubackend.auth;

import com.khutircraftubackend.seller.Seller;
import com.khutircraftubackend.auth.validation.annotation.PasswordMatches;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * Клас UserDTO використовується для передачі даних користувача.
 * <p>
 * Цей клас містить основні дані користувача, такі як ім'я, email, пароль, підтвердження паролю, підтвердження коду з пошти, роль, статус, токен тощо.
 * </p>
 */
@Builder
@PasswordMatches
public record UserDTO(

        @NotBlank(message = "E-mail не може бути порожнім")
        @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
        String email,

        @NotBlank(message = "Пароль не може бути порожнім")
        @Size(min = 8, max = 30, message = "Пароль має містити від 8 до 30 символів")
        @Pattern(regexp = "^[^!@#$%^&*()_+=]*$", message = "Пароль не може містити спеціальні символи !@#$%^&*()_+=")
        String password,

        @NotBlank(message = "Підтвердження паролю не може бути порожнім")
        @Size(min = 8, max = 30, message = "Пароль має містити від 8 до 30 символів")
        @Pattern(regexp = "^[^!@#$%^&*()_+=]*$", message = "Пароль не може містити спеціальні символи !@#$%^&*()_+=")
        String confirmPassword,

        String confirmationCode,

        @NotNull(message = "Роль не може бути порожньою")
        Role role,

        boolean enabled,

        String jwt,

        Seller seller

) {
        /**
         * Is password matching boolean.
         *
         * @return the boolean
         */
        public boolean isPasswordMatching() {
                return this.password != null && this.password.equals(this.confirmPassword);
        }

}

package com.khutircraftubackend.seller;

import com.khutircraftubackend.auth.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * Клас SellerDTO використовується для передачі даних продавця.
 * <p>
 * Цей клас містить основні дані користувача, такі як назва компанії, email, номер телефону, податковий номер тощо.
 * </p>
 */

@Builder
public record SellerDTO(

        @NotBlank(message = "Назва компанії не може бути порожнім")
        @Size(max = 200, message = "Назва компанії не може перевищувати 200 символів")
        @Pattern(regexp = "^[a-zA-Zа-яА-ЯіїєґІЇЄҐ\\s]*$", message = "Назва компанії має містити лише українські або англійські літери")
        String company,

        @NotBlank(message = "E-mail не може бути порожнім")
        @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
        String email,

        @NotBlank(message = "Номер телефону не може бути порожнім")
        @Pattern(regexp = "^\\+380\\d{9}$", message = "Номер телефону має бути у форматі +380__________")
        String phoneNumber,

        User user,

        String tax_code

) {
}

package com.khutircraftubackend.seller;

import com.khutircraftubackend.auth.UserEntity;
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
        @Size(max = 100, message = "Назва компанії не може перевищувати 100 символів")
        @Pattern(regexp = "^[a-zA-Zа-яА-ЯіїєґІЇЄҐ\\s]*$", message = "Назва компанії має містити лише українські або англійські літери")
        String companyName,

        @NotBlank(message = "Ім'я не може бути порожнім")
        @Size ( max = 100, message = "Ім'я не може перевищувати 100 символів")
        String sellerName,

        @Size (min = 12, max = 13)
        @Pattern(regexp = "^\\+380\\d{9}$", message = "Номер телефону має бути у форматі +380__________")
        String phoneNumber
) {
}

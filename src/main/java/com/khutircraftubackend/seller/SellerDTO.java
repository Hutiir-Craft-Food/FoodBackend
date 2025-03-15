package com.khutircraftubackend.seller;

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

        @NotBlank (message = "Ім'я не може бути порожнім")
        @Size (max = 100, message = "Ім'я не може перевищувати 100 символів")
        String sellerName
) {
}

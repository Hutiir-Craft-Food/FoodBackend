package com.khutircraftubackend.seller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record SellerDTO(

        @NotBlank (message = "Назва компанії(ПІБ) не може бути порожнім")
        @Size (max = 100, message = "Ім'я не може перевищувати 100 символів")
        @Pattern(regexp = "^[a-zA-Zа-яА-ЯІіЇїЄєҐґ\\d&,`'\\-\\s]+$", message = "Назва компанії має містити лише " +
                "українські або англійські літери, цифри і знаки \"&-,'`\"")
        String sellerName
) {
}

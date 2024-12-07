package com.khutircraftubackend.seller.request;

import com.khutircraftubackend.address.AddressRequest;
import com.khutircraftubackend.delivery.DeliveryMethodRequest;
import com.khutircraftubackend.seller.qualityCertificates.QualityCertificateRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.Collection;

/**
 * Клас SellerRequest використовується для передачі даних продавця.
 * <p>
 * Цей клас містить основні дані користувача, такі як назва компанії, email, номер телефону, податковий номер тощо.
 * </p>
 */

@Builder
public record SellerRequest(

        @NotBlank(message = "Назва компанії не може бути порожнім")
        @Size(max = 100, message = "Назва компанії не може перевищувати 100 символів")
        @Pattern(regexp = "^[a-zA-Zа-яА-ЯіїєґІЇЄҐ\\s]*$", message = "Назва компанії має містити лише українські або англійські літери")
        String companyName,

        @NotBlank(message = "Ім'я не може бути порожнім")
        @Size ( max = 100, message = "Ім'я не може перевищувати 100 символів")
        String sellerName,

        @Size (min = 12, max = 13)
        @Pattern(regexp = "^\\+380\\d{9}$", message = "Номер телефону має бути у форматі +380__________")
        String phoneNumber,

        @Size (min = 12, max = 13)
        @Pattern(regexp = "^\\+380\\d{9}$", message = "Номер телефону має бути у форматі +380__________")
        String customerPhoneNumber,
        String description,
        
        Collection<QualityCertificateRequest> qualityCertificatesUrl,
        Collection<DeliveryMethodRequest> deliveryMethods,
        AddressRequest addressRequest
) {
}

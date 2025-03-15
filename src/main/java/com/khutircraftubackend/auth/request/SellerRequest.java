//package com.khutircraftubackend.auth.request;
//
//import com.khutircraftubackend.seller.SellerDTO;
//import com.khutircraftubackend.user.Role;
//import io.swagger.v3.oas.annotations.media.Schema;
//import jakarta.validation.Valid;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Pattern;
//import jakarta.validation.constraints.Size;
//
//@Schema(description = "Запрос для регистрации продавца")
//public record SellerRequest(
//        @NotBlank(message = "E-mail не може бути порожнім")
//        @Pattern(regexp = "^[A-Za-z\\d+_.-]+@[A-Za-z\\d.-]+\\.[A-Za-z]{2,}$")
//        @Schema(description = "Email пользователя", example = "seller@example.com")
//        String email,
//
//        @NotBlank(message = "Пароль не може бути порожнім")
//        @Size(min = 8, max = 30, message = "Пароль має містити від 8 до 30 символів")
//        @Pattern(regexp = "^[^!@#$%^&*()_+=]*$", message = "Пароль не може містити спеціальні символи !@#$%^&*()_+=")
//        @Schema(description = "Пароль пользователя", example = "password123")
//        String password,
//
//        @NotNull(message = "Роль не може бути порожньою.")
//        @Schema(description = "Роль пользователя", example = "SELLER")
//        Role role,
//
//        @NotNull(message = "Розсилка не може бути порожньою.")
//        @Schema(description = "Согласие на получение рекламы", example = "true")
//        Boolean isReceiveAdvertising,
//
//        @NotNull(message = "Детали продавца не можуть бути порожніми.")
//        @Schema(description = "Детали продавца")
//        SellerDTO details
//) implements RegisterRequest {
//}

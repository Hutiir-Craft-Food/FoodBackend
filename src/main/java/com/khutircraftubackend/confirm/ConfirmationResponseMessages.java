package com.khutircraftubackend.confirm;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ConfirmationResponseMessages {

    public static final String CONFIRMATION_TOKEN_INVALID = "Неправильний токен підтвердження пошти.";
    public static final String CONFIRMATION_TOKEN_EXPIRED = "Час дії токена підтвердження пошти вичерпано.";
    public static final String EMAIL_CONFIRMED = "Ваш email підтверджено.";
    public static final String EMAIL_ALREADY_CONFIRMED = "Ваш email вже підтверджено.";
    public static final String VERIFICATION_CODE_SUBJECT = "Підтвердження реєстрації.";
    public static final String VERIFICATION_CODE_TEXT = "Будь ласка, підтвердіть вашу реєстрацію. Введіть 6-значний код %s.";
    public static final String WAIT_FOR_NEXT_ATTEMPT = "Час для повторного отримання кода ще не настав. Зачекайте.";
}

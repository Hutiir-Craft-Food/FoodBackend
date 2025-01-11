package com.khutircraftubackend.confirm;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class ConfirmResponseMessages {

    public static final String EMAIL_ACCEPTED = "Ваш email вже підтверджено.";
    public static final String CONFIRM_NOT_FOUND = "Неправильний токен підтвердження пошти.";
    public static final String CONFIRM_TIME_IS_UP = "Час дії токена підтвердження пошти вичерпано.";
    public static final String CONFIRM_ACCEPTED = "Ваш email підтверджено.";
    public static final String VERIFICATION_CODE_SUBJECT = "Підтвердження реєстрації.";
    public static final String VERIFICATION_CODE_TEXT =
            "Будь ласка, підтвердіть вашу реєстрацію. Введіть 6-значний код %s.";

}

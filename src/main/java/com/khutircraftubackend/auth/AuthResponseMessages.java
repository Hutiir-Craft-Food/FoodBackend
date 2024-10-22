package com.khutircraftubackend.auth;

public final class AuthResponseMessages {

    public static final String USER_BLOCKED = "Користувач з поштою %s заблокований.";
    public static final String EMAIL_IS_ALREADY_IN_USE =
            "Цей email зайнятий, використайте інший або ввійдіть під цим";
    public static final String VERIFICATION_CODE_SUBJECT = "Підтвердження реєстрації. ";
    public static final String VERIFICATION_CODE_TEXT =
            "Будь ласка, підтвердіть вашу реєстрацію. Вести 6 значний код %s ";
    public static final String RECOVERY_PASSWORD_SUBJECT = "Відновлення паролю";
    public static final String RECOVERY_PASSWORD_TEXT = "Ваш тимчасовий пароль: %s";

    private AuthResponseMessages(){}
}

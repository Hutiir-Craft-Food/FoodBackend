package com.khutircraftubackend.auth;

public final class AuthResponseMessages {

    public static final String USER_NOT_FOUND = "Користувач з таким email : %s не знайдений.";
    public static final String USER_BLOCKED = "Користувач з поштою %s заблокований.";
    public static final String USER_ENABLED = "Вітаємо з успішним входом.";
    public static final String EMAIL_IS_ALREADY_IN_USE =
            "Цей email зайнятий, використайте інший або ввійдіть під цим";
    public static final String VERIFICATION_CODE_SUBJECT = "Підтвердження реєстрації. ";
    public static final String VERIFICATION_CODE_TEXT =
            "Будь ласка, підтвердіть вашу реєстрацію. Вести 6 значний код %s ";
    public static final String NOT_VALID_CONFIRMATION_TOKEN = "Неправильний код підтвердження.";
    public static final String LIFE_IS_AFTER = "Час життя токена минув";
    public static final String RE_UPDATE_TOKEN = "Зачекайте 2 хвилини";
    public static final String RECOVERY_PASSWORD_SUBJECT = "Відновлення паролю";
    public static final String RECOVERY_PASSWORD_TEXT = "Ваш тимчасовий пароль: %s";
    public static final String REGISTER_GO_EMAIL = "Завершіть реєстрацію підтвердженням з переходом на пошту";

    private AuthResponseMessages(){}
}

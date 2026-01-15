package com.khutircraftubackend.auth.exception;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class AuthResponseMessages {

    public static final String AUTH_USER_BLOCKED = "Користувач заблокований";
    public static final String AUTH_INVALID_CREDENTIALS = "Невірні облікові данні";
    public static final String REGISTRATION_INVALID_REQUEST = "Помилка реєстрації. Перевірте введені дані";
    public static final String AUTH_CODE_SUBJECT = "Повідомлення про спробу реєстрації.";
    public static final String AUTH_CODE_TEXT = "На ваш email була зроблена cпроба реєстрації." +
        " Якщо це ви, проігноруйте листа. Інакше, зверніться до служби підтримки Khutir Craft.";
}

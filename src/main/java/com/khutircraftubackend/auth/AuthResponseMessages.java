package com.khutircraftubackend.auth;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class AuthResponseMessages {

    public static final String USER_BLOCKED = "Помилка аутентифікації. Користувач з таким email %s заблокований";
    public static final String USER_EXISTS = "Помилка аутентифікації. Перевірте облікові данні";
    public static final String AUTH_CODE_SUBJECT = "Помилкова реєстрації.";
    public static final String AUTH_CODE_TEXT = "На ваш email була зроблена cпроба реєстрації." +
        " Якщо це ви, проігноруйте листа. Якщо нi, зверніться до служби підтримки Hutir Craft.";
}

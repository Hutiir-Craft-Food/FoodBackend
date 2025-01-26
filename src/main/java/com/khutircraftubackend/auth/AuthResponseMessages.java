package com.khutircraftubackend.auth;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class AuthResponseMessages {

    public static final String USER_BLOCKED = "Користувач з поштою %s заблокований.";
    public static final String USER_CONFLICT = "Регістрація успішна пройдена";
    public static final String AUTH_CODE_SUBJECT = "Помилкова реєстрації.";
    public static final String AUTH_CODE_TEXT =
            "На ваш емаїл була зроблена проба реєстрації." +
            " Якщо це ви проігноруйте листа. Якщо не зверніться до служби підтримки Hutir Craft.";
}

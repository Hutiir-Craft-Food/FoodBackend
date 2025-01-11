package com.khutircraftubackend.auth;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class AuthResponseMessages {
    public static final String USER_BLOCKED = "Користувач з поштою %s заблокований.";
    public static final String EMAIL_IS_ALREADY_IN_USE = "Цей email зайнятий, використайте інший або ввійдіть під цим";
}

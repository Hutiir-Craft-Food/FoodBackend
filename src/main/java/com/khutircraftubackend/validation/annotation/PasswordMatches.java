package com.khutircraftubackend.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Анотація PasswordMatches використовується для валідації того, що паролі співпадають.
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = com.khutircraftubackend.validation.validator.PasswordMatchesValidator.class)
public @interface PasswordMatches {

    String message() default "Пароль і підтвердження не співпадають";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

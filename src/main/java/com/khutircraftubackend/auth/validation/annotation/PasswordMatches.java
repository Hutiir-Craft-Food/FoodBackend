package com.khutircraftubackend.auth.validation.annotation;

import com.khutircraftubackend.auth.validation.validator.PasswordMatchesValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Анотація PasswordMatches використовується для валідації того, що паролі співпадають.
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchesValidator.class)
@Documented
public @interface PasswordMatches {

    String message() default "Пароль і підтвердження не співпадають";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

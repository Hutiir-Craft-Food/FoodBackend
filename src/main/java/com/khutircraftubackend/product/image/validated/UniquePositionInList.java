package com.khutircraftubackend.product.image.validated;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = UniquePositionInListValidator.class)
public @interface UniquePositionInList {
    String message() default "Позиції мають бути унікальними.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
package com.khutircraftubackend.validation.validator;

import com.khutircraftubackend.models.User;
import com.khutircraftubackend.validation.annotation.PasswordMatches;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Клас PasswordMatchesValidator реалізує логіку валідації для анотації PasswordMatches.
 */

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, User> {
    @Override
    public boolean isValid(User user, ConstraintValidatorContext context) {
        return user.getPassword().equals(user.getConfirmPassword());
    }
}

package com.khutircraftubackend.auth.validation.validator;

import com.khutircraftubackend.auth.UserDTO;
import com.khutircraftubackend.auth.validation.annotation.PasswordMatches;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Клас PasswordMatchesValidator реалізує логіку валідації для анотації PasswordMatches.
 */

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, UserDTO> {
    @Override
    public boolean isValid(UserDTO userDTO, ConstraintValidatorContext context) {
        return userDTO.isPasswordMatching();
    }

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }
}

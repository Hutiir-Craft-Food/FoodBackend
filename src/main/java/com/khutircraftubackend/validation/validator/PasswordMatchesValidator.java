package com.khutircraftubackend.validation.validator;

import com.khutircraftubackend.dto.UserDTO;
import com.khutircraftubackend.validation.annotation.PasswordMatches;
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

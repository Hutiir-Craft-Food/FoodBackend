package com.khutircraftubackend.validation.validator;

import com.khutircraftubackend.dto.SellerDTO;
import com.khutircraftubackend.validation.annotation.PasswordMatches;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, SellerDTO> {
    @Override
    public boolean isValid(SellerDTO sellerDTO, ConstraintValidatorContext context) {
        return sellerDTO.isPasswordMatching();
    }
}

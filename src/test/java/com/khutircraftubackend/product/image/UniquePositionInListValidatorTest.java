package com.khutircraftubackend.product.image;

import com.khutircraftubackend.product.image.request.ProductImageUploadRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class UniquePositionInListValidatorTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void shouldFailValidation_whenDuplicatePositionExists() {
        ProductImageUploadRequest request = new ProductImageUploadRequest(
                List.of(
                        new ProductImageUploadRequest.UploadImageInfo(1),
                        new ProductImageUploadRequest.UploadImageInfo( 1)));

        Set<ConstraintViolation<ProductImageUploadRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());

        boolean containsPositionError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("Позиції мають бути унікальними."));

        assertTrue(containsPositionError);
    }

    @Test
    void shouldPassValidation_whenPositionsUnique() {
        ProductImageUploadRequest request = new ProductImageUploadRequest(
                List.of(
                        new ProductImageUploadRequest.UploadImageInfo(1),
                        new ProductImageUploadRequest.UploadImageInfo(2)));

        Set<ConstraintViolation<ProductImageUploadRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }
}

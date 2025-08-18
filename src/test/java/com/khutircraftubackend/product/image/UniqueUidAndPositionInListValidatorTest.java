package com.khutircraftubackend.product.image;

import com.khutircraftubackend.product.image.request.ProductImagesChanges;
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
class UniqueUidAndPositionInListValidatorTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void shouldFailValidation_whenDuplicateUidExists() {
        ProductImagesChanges request = new ProductImagesChanges(
                List.of(
                        new ProductImagesChanges.Images("uid1", 0),
                        new ProductImagesChanges.Images("uid1", 1)));

        Set<ConstraintViolation<ProductImagesChanges>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());

        boolean containsUidError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("UID uid1 дублюється"));

        assertTrue(containsUidError);
    }

    @Test
    void shouldFailValidation_whenDuplicatePositionExists() {
        ProductImagesChanges request = new ProductImagesChanges(
                List.of(
                        new ProductImagesChanges.Images("uid1", 0),
                        new ProductImagesChanges.Images("uid2", 0)));

        Set<ConstraintViolation<ProductImagesChanges>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());

        boolean containsPositionError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("Position 0 дублюється"));

        assertTrue(containsPositionError);
    }

    @Test
    void shouldPassValidation_whenUidsAndPositionsUnique() {
        ProductImagesChanges request = new ProductImagesChanges(
                List.of(
                        new ProductImagesChanges.Images("uid1", 0),
                        new ProductImagesChanges.Images("uid2", 1)));

        Set<ConstraintViolation<ProductImagesChanges>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }
}

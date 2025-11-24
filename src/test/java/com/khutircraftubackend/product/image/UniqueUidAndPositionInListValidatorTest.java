package com.khutircraftubackend.product.image;

import com.khutircraftubackend.product.image.request.ProductImageChangeRequest;
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
class UniqueUidAndPositionInListValidatorTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

//    @Test
//    void shouldFailValidation_whenDuplicateUidExists() {
//        ProductImageChangeRequest request = new ProductImageChangeRequest(
//                List.of(
//                        new ProductImageChangeRequest.Image(1l, 0),
//                        new ProductImageChangeRequest.Image(1l, 1)));
//
//        Set<ConstraintViolation<ProductImageChangeRequest>> violations = validator.validate(request);
//
//        assertFalse(violations.isEmpty());
//
//        boolean containsUidError = violations.stream()
//                .anyMatch(v -> v.getMessage().contains("ID мають бути унікальними."));
//
//        assertTrue(containsUidError);
//    }

    @Test
    void shouldFailValidation_whenDuplicatePositionExists() {
        ProductImageUploadRequest request = new ProductImageUploadRequest(
                List.of(
                        new ProductImageUploadRequest.Image(0),
                        new ProductImageUploadRequest.Image( 0)));

        Set<ConstraintViolation<ProductImageUploadRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());

        boolean containsPositionError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("Позиції мають бути унікальними."));

        assertTrue(containsPositionError);
    }

//    @Test
//    void shouldPassValidation_whenUidsAndPositionsUnique() {
//        ProductImageChangeRequest request = new ProductImageChangeRequest(
//                List.of(
//                        new ProductImageChangeRequest.Image(1l, 0),
//                        new ProductImageChangeRequest.Image(2l, 1)));
//
//        Set<ConstraintViolation<ProductImageChangeRequest>> violations = validator.validate(request);
//
//        assertTrue(violations.isEmpty());
//    }

    @Test
    void shouldPassValidation_whenPositionsUnique() {
        ProductImageUploadRequest request = new ProductImageUploadRequest(
                List.of(
                        new ProductImageUploadRequest.Image(0),
                        new ProductImageUploadRequest.Image(1)));

        Set<ConstraintViolation<ProductImageUploadRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }
}

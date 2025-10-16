//package com.khutircraftubackend.product.image;
//
//import com.khutircraftubackend.product.image.request.ProductImagesChangeRequest;
//import jakarta.validation.ConstraintViolation;
//import jakarta.validation.Validation;
//import jakarta.validation.Validator;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//
//import java.util.List;
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@ExtendWith(MockitoExtension.class)
//class UniqueUidAndPositionInListValidatorTest {
//
//    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
//
//    @Test
//    void shouldFailValidation_whenDuplicateUidExists() {
//        ProductImagesChangeRequest request = new ProductImagesChangeRequest(
//                List.of(
//                        new ProductImagesChangeRequest.Image("uid1", 0),
//                        new ProductImagesChangeRequest.Image("uid1", 1)));
//
//        Set<ConstraintViolation<ProductImagesChangeRequest>> violations = validator.validate(request);
//
//        assertFalse(violations.isEmpty());
//
//        boolean containsUidError = violations.stream()
//                .anyMatch(v -> v.getMessage().contains("UID uid1 дублюється"));
//
//        assertTrue(containsUidError);
//    }
//
//    @Test
//    void shouldFailValidation_whenDuplicatePositionExists() {
//        ProductImagesChangeRequest request = new ProductImagesChangeRequest(
//                List.of(
//                        new ProductImagesChangeRequest.Image("uid1", 0),
//                        new ProductImagesChangeRequest.Image("uid2", 0)));
//
//        Set<ConstraintViolation<ProductImagesChangeRequest>> violations = validator.validate(request);
//
//        assertFalse(violations.isEmpty());
//
//        boolean containsPositionError = violations.stream()
//                .anyMatch(v -> v.getMessage().contains("Position 0 дублюється"));
//
//        assertTrue(containsPositionError);
//    }
//
//    @Test
//    void shouldPassValidation_whenUidsAndPositionsUnique() {
//        ProductImagesChangeRequest request = new ProductImagesChangeRequest(
//                List.of(
//                        new ProductImagesChangeRequest.Image("uid1", 0),
//                        new ProductImagesChangeRequest.Image("uid2", 1)));
//
//        Set<ConstraintViolation<ProductImagesChangeRequest>> violations = validator.validate(request);
//
//        assertTrue(violations.isEmpty());
//    }
//}

package com.khutircraftubackend.product.image;

import com.khutircraftubackend.product.image.exception.*;
import com.khutircraftubackend.product.image.request.ProductImageChangeRequest;
import com.khutircraftubackend.product.image.request.ProductImageUploadRequest;
import com.khutircraftubackend.product.image.response.ProductImageResponseMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ProductImageValidatorTest {

    private ProductImageValidator validator;
    private List<ProductImageEntity> existingImages;

    @BeforeEach
    void setUp() {
        validator = new ProductImageValidator();
        
        existingImages = List.of(
            ProductImageEntity.builder().id(1L).position(1).build(),
            ProductImageEntity.builder().id(2L).position(2).build(),
            ProductImageEntity.builder().id(3L).position(3).build()
        );
    }

    @Nested
    @DisplayName("Validate Delete Positions Tests")
    class DeletePositionsValidation {

        @Test
        void validateDeletePositions_WhenEmptyImages_ThrowsException() {
            List<ProductImageEntity> emptyImages = Collections.emptyList();
            List<Integer> positions = List.of(1);

            ImageNotFoundException ex = assertThrows(ImageNotFoundException.class,
                () -> validator.validateDeletePositions(positions, emptyImages));

            String msg = ex.getMessage();
            List<String> expected = List.of("1", "2", "3", "4", "5");
            expected.forEach(id -> assertTrue(msg.contains(id)));
        }

        @Test
        void validateDeletePositions_WhenNullPositions_ShouldNotThrow() {
            assertDoesNotThrow(() -> 
                validator.validateDeletePositions(null, existingImages));
        }

        @Test
        void validateDeletePositions_WhenEmptyPositions_ShouldNotThrow() {
            assertDoesNotThrow(() -> 
                validator.validateDeletePositions(Collections.emptyList(), existingImages));
        }

        @Test
        void validateDeletePositions_WhenInvalidPosition_ThrowsException() {
            List<Integer> invalidPositions = List.of(999);

            ImageValidationException ex = assertThrows(ImageValidationException.class,
                () -> validator.validateDeletePositions(invalidPositions, existingImages));

            assertTrue(ex.getMessage().contains("[999]"));
        }

        @Test
        void validateDeletePositions_WhenPositionNotFound_ThrowsException() {
            List<Integer> notFoundPositions = List.of(4);

            ImageNotFoundException ex = assertThrows(ImageNotFoundException.class,
                () -> validator.validateDeletePositions(notFoundPositions, existingImages));

            assertTrue(ex.getMessage().contains("[4]"));
        }

        @Test
        void validateDeletePositions_WhenValidPositions_ShouldNotThrow() {
            List<Integer> validPositions = List.of(1, 2);

            assertDoesNotThrow(() -> 
                validator.validateDeletePositions(validPositions, existingImages));
        }
    }

    @Nested
    @DisplayName("Validate Image IDs Tests")
    class ImageIdsValidation {

        @Test
        void validateImageIds_WhenAllIdsExist_ShouldNotThrow() {
            ProductImageChangeRequest request = new ProductImageChangeRequest(
                List.of(
                    new ProductImageChangeRequest.ChangeImageInfo(1L, 1),
                    new ProductImageChangeRequest.ChangeImageInfo(2L, 2),
                    new ProductImageChangeRequest.ChangeImageInfo(3L, 3)
                )
            );

            assertDoesNotThrow(() ->
                validator.validateImageIds(request, existingImages));
        }

        @Test
        void validateImageIds_WhenMissingIds_ThrowsException() {
            ProductImageChangeRequest request = new ProductImageChangeRequest(
                List.of(
                    new ProductImageChangeRequest.ChangeImageInfo(1L, 1),
                    new ProductImageChangeRequest.ChangeImageInfo(999L, 2)
                )
            );

            ImageNotFoundException ex = assertThrows(ImageNotFoundException.class,
                () -> validator.validateImageIds(request, existingImages));

            assertTrue(ex.getMessage().contains("[999]"));
        }

        @Test
        void validateImageIds_WhenCountMismatch_ThrowsException() {
            ProductImageChangeRequest request = new ProductImageChangeRequest(
                List.of(
                    new ProductImageChangeRequest.ChangeImageInfo(1L, 1)
                )
            );

            ImagesCountMismatchException ex = assertThrows(ImagesCountMismatchException.class,
                () -> validator.validateImageIds(request, existingImages));

            assertTrue(ex.getMessage().contains("1"));
            assertTrue(ex.getMessage().contains("3"));
        }
    }

    @Nested
    @DisplayName("Validate Upload Request Tests")
    class UploadRequestValidation {

        @Test
        void validateUploadRequest_WhenTooManyFiles_ThrowsException() {
            ProductImageUploadRequest request = new ProductImageUploadRequest(
                List.of(new ProductImageUploadRequest.UploadImageInfo(3))
            );
            List<MultipartFile> tooManyFiles = Collections.nCopies(6, mock(MultipartFile.class));

            TooManyImagesException ex = assertThrows(TooManyImagesException.class,
                () -> validator.validateUploadRequest(existingImages, request, tooManyFiles));

            assertTrue(ex.getMessage().contains("5"));
        }

        @Test
        void validateUploadRequest_WhenFilesCountMismatch_ThrowsException() {
            ProductImageUploadRequest request = new ProductImageUploadRequest(
                List.of(new ProductImageUploadRequest.UploadImageInfo(3))
            );
            List<MultipartFile> files = Collections.nCopies(2, mock(MultipartFile.class));

            ImagesCountMismatchException ex = assertThrows(ImagesCountMismatchException.class,
                () -> validator.validateUploadRequest(existingImages, request, files));

            assertTrue(ex.getMessage().contains("2"));
            assertTrue(ex.getMessage().contains("1"));
        }

        @Test
        void validateUploadRequest_WhenDuplicatePosition_ThrowsException() {
            ProductImageUploadRequest request = new ProductImageUploadRequest(
                List.of(new ProductImageUploadRequest.UploadImageInfo(1))
            );
            List<MultipartFile> files = List.of(mock(MultipartFile.class));

            PositionAlreadyExistsException ex = assertThrows(PositionAlreadyExistsException.class,
                () -> validator.validateUploadRequest(existingImages, request, files));

            assertEquals(ProductImageResponseMessages.ERROR_POSITION_ALREADY_EXISTS, ex.getMessage());
        }

        @Test
        void validateUploadRequest_WhenValidRequest_ShouldNotThrow() {
            ProductImageUploadRequest request = new ProductImageUploadRequest(
                List.of(new ProductImageUploadRequest.UploadImageInfo(4))
            );
            List<MultipartFile> files = List.of(mock(MultipartFile.class));

            assertDoesNotThrow(() -> 
                validator.validateUploadRequest(existingImages, request, files));
        }
    }
}
package com.khutircraftubackend.product.image;

import com.khutircraftubackend.product.ProductEntity;
import com.khutircraftubackend.product.ProductService;
import com.khutircraftubackend.product.exception.ProductNotFoundException;
import com.khutircraftubackend.product.image.exception.*;
import com.khutircraftubackend.product.image.request.ProductImageUploadRequest;
import com.khutircraftubackend.product.image.request.ProductImageChangeRequest;
import com.khutircraftubackend.product.image.response.ProductImageDTO;
import com.khutircraftubackend.product.image.response.ProductImageResponse;
import com.khutircraftubackend.product.image.response.ProductImageResponseMessages;
import com.khutircraftubackend.storage.StorageService;
import com.khutircraftubackend.validated.ImageMimeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static com.khutircraftubackend.product.exception.ProductResponseMessage.PRODUCT_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductImagesServiceTest {

    @Mock
    private ProductImageRepository imageRepository;
    @Mock
    private ProductService productService;
    @Mock
    private ProductImageMapper imageMapper;
    @Mock
    private StorageService storageService;
    @Mock
    private ImageMimeValidator mimeValidator;
    @Mock
    private ProductImageValidator validator;

    @InjectMocks
    private ProductImageService imagesService;

    private ProductEntity product;
    private final List<ProductImageEntity> imagesList = new ArrayList<>();
    private final List<ProductImageDTO> dtoList = new ArrayList<>();
    private static final Long MAX_COUNT_FILES = 5L;

    @BeforeEach
    void setup() {
        product = ProductEntity.builder()
                .id(1L)
                .name("Test Product")
                .build();

        ProductImageEntity image1 = ProductImageEntity.builder()
                .id(1L)
                .position(0)
                .product(product)
                .variants(createVariants("link1"))
                .build();

        ProductImageEntity image2 = ProductImageEntity.builder()
                .id(2L)
                .position(1)
                .product(product)
                .variants(createVariants("link2"))
                .build();

        imagesList.add(image1);
        imagesList.add(image2);

        dtoList.add(
                ProductImageDTO.builder()
                        .id(1L)
                        .productId(1L)
                        .position(0)
                        .links(null)
                        .build());
        dtoList.add(ProductImageDTO.builder()
                        .id(2L)
                        .productId(1L)
                        .position(1)
                        .links(null)
                        .build()
        );
    }

    private List<ProductImageVariant> createVariants(String baseLink) {
        return Arrays.stream(ImageSize.values())
                .map(size -> ProductImageVariant.builder()
                        .tsSize(size)
                        .link(baseLink + "_" + size.name().toLowerCase())
                        .build())
                .toList();
    }

    @Nested
    @DisplayName("Product image viewing tests")
    class ViewImages {

        @Test
        void productImagesViewSuccess() {
            when(imageRepository.findByProductId(1L)).thenReturn(imagesList);
            when(imageMapper.toProductImageDtoList(imagesList)).thenReturn(dtoList);

            ProductImageResponse actual = imagesService.getImages(1L);

            assertEquals(dtoList, actual.images());
            verify(productService).findProductById(1L);
            verify(imageRepository).findByProductId(1L);
            verify(imageMapper).toProductImageDtoList(imagesList);
        }

        @Test
        void shouldThrowNotFoundWhenProductDoesNotExist() {
            Long productId = 2L;
            when(productService.findProductById(productId))
                    .thenThrow(new ProductNotFoundException(String.format(PRODUCT_NOT_FOUND, productId)));

            ProductNotFoundException ex = assertThrows(ProductNotFoundException.class,
                    () -> imagesService.getImages(2L));

            assertEquals(String.format(PRODUCT_NOT_FOUND, productId), ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Product image upload tests")
    class UploadImages {

        @Test
        void uploadImagesSuccess() {
            // Given
            ProductImageUploadRequest request = new ProductImageUploadRequest(
                    List.of(new ProductImageUploadRequest.ImageUpload(2))
            );
            MultipartFile file = mock(MultipartFile.class);
            List<MultipartFile> files = List.of(file);

            ProductImageEntity newImage = ProductImageEntity.builder()
                    .id(3L)
                    .position(2)
                    .product(product)
                    .variants(createVariants("link3"))
                    .build();

            ProductImageDTO newDtoList = ProductImageDTO.builder()
                    .id(2L)
                    .productId(1L)
                    .position(1)
                    .links(null)
                    .build();

            dtoList.add(newDtoList);
            List<ProductImageEntity> allImagesAfterUpload = new ArrayList<>(imagesList);
            allImagesAfterUpload.add(newImage);

            when(imageRepository.findByProductId(1L)).thenReturn(imagesList);
            doNothing().when(validator).validateUploadRequest(imagesList, request, files);
            when(imageRepository.saveAll(anyList())).thenReturn(List.of(newImage));
            when(imageMapper.toProductImageDtoList(allImagesAfterUpload)).thenReturn(dtoList);

            ProductImageResponse actual = imagesService.uploadImages(1L, request, files);

            assertNotNull(actual);
            verify(validator).validateUploadRequest(imagesList, request, files);
            verify(imageRepository).saveAll(anyList());
        }

        @Test
        void upload_WhenFilesExceedMaxCount_ThrowsTooManyImagesException() {
            ProductImageUploadRequest request = new ProductImageUploadRequest(
                    List.of(new ProductImageUploadRequest.ImageUpload(2))
            );

            List<MultipartFile> files = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                files.add(mock(MultipartFile.class));
            }

            doThrow(new TooManyImagesException(String.format(
                    ProductImageResponseMessages.ERROR_TOO_MANY_IMAGES, MAX_COUNT_FILES)))
                    .when(validator).validateUploadRequest(anyList(), eq(request), eq(files));

            TooManyImagesException ex = assertThrows(TooManyImagesException.class,
                    () -> imagesService.uploadImages(1L, request, files));

            assertTrue(ex.getMessage().contains("5"));
        }

        @Test
        void upload_WhenDuplicatePosition_ThrowsPositionAlreadyExistsException() {
            ProductImageUploadRequest request = new ProductImageUploadRequest(
                    List.of(new ProductImageUploadRequest.ImageUpload(1))
            );
            MultipartFile file = mock(MultipartFile.class);
            List<MultipartFile> files = List.of(file);

            when(imageRepository.findByProductId(1L)).thenReturn(imagesList);
            doThrow(new PositionAlreadyExistsException(ProductImageResponseMessages.ERROR_POSITION_ALREADY_EXISTS))
                    .when(validator).validateUploadRequest(imagesList, request, files);

            PositionAlreadyExistsException ex = assertThrows(PositionAlreadyExistsException.class,
                    () -> imagesService.uploadImages(1L, request, files));

            assertEquals(ProductImageResponseMessages.ERROR_POSITION_ALREADY_EXISTS, ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Product image reorder tests")
    class ReorderImages {

        @Test
        void reorderImagesSuccess() {
            ProductImageChangeRequest request = new ProductImageChangeRequest(
                    List.of(
                            new ProductImageChangeRequest.Image(1L, 1),
                            new ProductImageChangeRequest.Image(2L, 0)
                    )
            );

            when(imageRepository.findByProductId(1L)).thenReturn(imagesList);
            doNothing().when(validator).validateImageIds(request, imagesList);

            ProductImageResponse actual = imagesService.reorderImages(1L, request);

            assertNotNull(actual);
            verify(validator).validateImageIds(request, imagesList);
        }

        @Test
        void reorder_WhenImageNotFound_ThrowsImageNotFoundException() {
            ProductImageChangeRequest request = new ProductImageChangeRequest(
                    List.of(new ProductImageChangeRequest.Image(999L, 0))
            );

            when(imageRepository.findByProductId(1L)).thenReturn(imagesList);
            doThrow(new ImageNotFoundException(
                    String.format(ProductImageResponseMessages.ERROR_IMAGE_NOT_FOUND_BY_ID, 999L)))
                    .when(validator).validateImageIds(request, imagesList);

            ImageNotFoundException ex = assertThrows(ImageNotFoundException.class,
                    () -> imagesService.reorderImages(1L, request));

            assertNotNull(ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Product image deletion tests")
    class DeleteImages {

        @Test
        void deleteAllImagesSuccess() {
            List<Integer> emptyList = Collections.emptyList();

            when(imageRepository.findByProductId(1L)).thenReturn(imagesList);
            doNothing().when(validator).validateDeletePositions(emptyList, imagesList);
            doNothing().when(storageService).deleteByUrl(anyString());

            imagesService.deleteImages(1L, emptyList);

            verify(storageService,
                    times(imagesList.size() * ImageSize.values().length)).deleteByUrl(anyString());
            verify(imageRepository).deleteAll(imagesList);
        }

        @Test
        void deletePositionImagesSuccess() {
            List<Integer> positions = List.of(1);

            when(imageRepository.findByProductId(1L)).thenReturn(imagesList);
            doNothing().when(validator).validateDeletePositions(positions, imagesList);
            doNothing().when(storageService).deleteByUrl(anyString());

            imagesService.deleteImages(1L, positions);

            verify(storageService, times(ImageSize.values().length)).deleteByUrl(anyString());
            verify(imageRepository).deleteAll(List.of(imagesList.get(1)));
        }

        @Test
        void delete_WhenInvalidPosition_ThrowsImageValidationException() {
            List<Integer> invalidPositions = List.of(999);

            when(productService.findProductById(1L)).thenReturn(product);
            when(imageRepository.findByProductId(1L)).thenReturn(imagesList);
            doThrow(new ImageValidationException(
                    String.format(ProductImageResponseMessages.ERROR_NOT_FOUND_POSITION, invalidPositions)))
                    .when(validator).validateDeletePositions(invalidPositions, imagesList);

            ImageValidationException ex = assertThrows(ImageValidationException.class,
                    () -> imagesService.deleteImages(1L, invalidPositions));

            assertNotNull(ex.getMessage());
        }
    }
}

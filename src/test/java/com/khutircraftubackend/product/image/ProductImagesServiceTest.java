package com.khutircraftubackend.product.image;

import com.khutircraftubackend.exception.httpstatus.BadRequestException;
import com.khutircraftubackend.exception.httpstatus.NotFoundException;
import com.khutircraftubackend.product.ProductEntity;
import com.khutircraftubackend.product.ProductService;
import com.khutircraftubackend.product.image.request.ProductImageUploadRequest;
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

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductImagesServiceTest {

    @Mock
    private ProductImagesRepository imageRepository;
    @Mock
    private ProductService productService;
    @InjectMocks
    private ProductImagesService imagesService;
    @Mock
    private ProductImageMapper imageMapper;
    @Mock
    private ProductImageResponse responseDto;
    @Mock
    private StorageService storageService;
    @Mock
    private ImageMimeValidator mimeValidator;
    private ProductEntity product;
    private List<ProductImageEntity> imagesList;
    private static final Long MAX_COUNT_FILES = 5L;


    @BeforeEach
    void setup() {
        product = ProductEntity.builder()
                .id(1L)
                .name("Test Product")
                .build();

        ProductImageEntity image1 = ProductImageEntity.builder()
                .id(1L)
                .link("test link 1")
                .position(0)
                .uid("pic0.jpeg")
                .tsSize(ImageSizes.LARGE)
                .product(product)
                .build();

        ProductImageEntity image2 = ProductImageEntity.builder()
                .id(2L)
                .link("test link 2")
                .position(1)
                .uid("pic1.jpeg")
                .tsSize(ImageSizes.LARGE)
                .product(product)
                .build();

        imagesList = List.of(image1, image2);
    }

    @Nested
    @DisplayName("Product image viewing tests")
    class ViewImages {

        @Test
        void productImagesViewSuccess() {
            when(productService.findProductById(1L)).thenReturn(product);
            when(imageRepository.findByProductId(1L)).thenReturn(imagesList);
            when(imageMapper.toResponseDto(imagesList)).thenReturn(responseDto);

            ProductImageResponse actual = imagesService.getProductImages(1L);

            assertEquals(responseDto, actual);
            verify(productService).findProductById(1L);
            verify(imageRepository).findByProductId(1L);
            verify(imageMapper).toResponseDto(imagesList);
        }

        @Test
        void shouldThrowNotFoundWhenProductDoesNotExist() {
            when(productService.findProductById(2L))
                    .thenThrow(new NotFoundException("Product with id 2 not found"));

            NotFoundException ex = assertThrows(NotFoundException.class, () -> imagesService.getProductImages(2L));

            assertEquals("Product with id 2 not found", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Product image loading Tests")
    class UploadImages {

        @Test
        void uploadImagesSuccess() {
            doNothing().when(mimeValidator).validateMimeTypes(anyList(), anyString());
            ProductImageUploadRequest jsonImages = new ProductImageUploadRequest(
                    List.of(new ProductImageUploadRequest.Image("pic2.jpeg", 2))
            );

            MultipartFile files = mock(MultipartFile.class);
            when(files.getOriginalFilename()).thenReturn("pic2.jpeg");
            List<MultipartFile> fileImages = List.of(files);

            ProductImageEntity savedEntity = ProductImageEntity.builder()
                    .id(3L)
                    .uid("pic2.jpeg")
                    .position(2)
                    .tsSize(ImageSizes.LARGE)
                    .link("test-link 3")
                    .build();

            List<ProductImageEntity> savedEntities = new ArrayList<>(imagesList);
            savedEntities.add(savedEntity);

            ProductImageResponse expected = new ProductImageResponse(
                    savedEntities.stream().map(img -> new ProductImageResponse.Image(
                            img.getUid(), img.getLink(), img.getTsSize(), img.getPosition()
                    )).toList()
            );

            when(productService.findProductById(1L)).thenReturn(product);
            when(imageRepository.saveAll(anyList())).thenReturn(savedEntities);
            when(imageMapper.toResponseDto(savedEntities)).thenReturn(expected);

            ProductImageResponse actual = imagesService.uploadImages(1L, jsonImages, fileImages);

            assertEquals(expected, actual);
            verify(productService).findProductById(1L);
            verify(imageRepository).saveAll(anyList());
            verify(imageMapper).toResponseDto(anyList());
        }

        @Test
        void upload_WhenFilesExceedMaxCount_ThrowsBadRequestException() {
            doNothing().when(mimeValidator).validateMimeTypes(anyList(), anyString());
            ProductImageUploadRequest jsonImages = new ProductImageUploadRequest(
                    List.of(new ProductImageUploadRequest.Image("pic2.jpeg", 2))
            );

            List<MultipartFile> fileImages = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                MultipartFile files = mock(MultipartFile.class);
                fileImages.add(files);
            }

            BadRequestException ex = assertThrows(BadRequestException.class, () ->
                    imagesService.uploadImages(1L, jsonImages, fileImages));

            assertEquals(String.format(
                    ProductImageResponseMessages.ERROR_TOO_MANY_IMAGES, MAX_COUNT_FILES), ex.getMessage());
        }

        @Test
        void validateNoDuplicates_WhenPositionAlreadyExistsInRequest_ThrowsException() {
            doNothing().when(mimeValidator).validateMimeTypes(anyList(), anyString());
            ProductImageUploadRequest jsonImages = new ProductImageUploadRequest(
                    List.of(new ProductImageUploadRequest.Image("pic2.jpeg", 1)));

            MultipartFile files = mock(MultipartFile.class);
            List<MultipartFile> fileImages = List.of(files);

            when(imageRepository.findByProductId(1L)).thenReturn(imagesList);

            BadRequestException ex = assertThrows(BadRequestException.class, () ->
                    imagesService.uploadImages(1L, jsonImages, fileImages));

            assertEquals(ProductImageResponseMessages.ERROR_POSITION_EXIST, ex.getMessage());
        }

        @Test
        void validateNoDuplicates_WhenUidAlreadyExists_ThrowsException() {
            doNothing().when(mimeValidator).validateMimeTypes(anyList(), anyString());
            ProductImageUploadRequest jsonImages = new ProductImageUploadRequest(
                    List.of(new ProductImageUploadRequest.Image("pic1.jpeg", 2)));

            MultipartFile files = mock(MultipartFile.class);
            List<MultipartFile> fileImages = List.of(files);

            when(imageRepository.findByProductId(1L)).thenReturn(imagesList);

            BadRequestException ex = assertThrows(BadRequestException.class, () ->
                    imagesService.uploadImages(1L, jsonImages, fileImages));

            assertEquals(ProductImageResponseMessages.ERROR_UID_EXIST, ex.getMessage());
        }

        @Test
        void uploadImages_WhenFileUidNotFoundInRequest_ThrowsException() {
            doNothing().when(mimeValidator).validateMimeTypes(anyList(), anyString());
            ProductImageUploadRequest jsonImages = new ProductImageUploadRequest(
                    List.of(new ProductImageUploadRequest.Image("pic2.jpeg", 2))
            );

            MultipartFile files = mock(MultipartFile.class);
            List<MultipartFile> fileImages = List.of(files);

            BadRequestException ex = assertThrows(BadRequestException.class, () ->
                    imagesService.uploadImages(1L, jsonImages, fileImages));

            assertEquals(String.format(ProductImageResponseMessages.ERROR_NAME_NOT_FOUND,
                    jsonImages.images().get(0).uid()), ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Tests for changes in product images")
    class ChangesImages {

        @Test
        void changesImagesSuccess() {
            ProductImageUploadRequest jsonImages = new ProductImageUploadRequest(
                    List.of(new ProductImageUploadRequest.Image("pic0", 1),
                            new ProductImageUploadRequest.Image("pic1", 0))
            );

            List<ProductImageEntity> allImages = List.of(
                ProductImageEntity.builder().uid("pic0").tsSize(ImageSizes.LARGE).position(0).link("link0_s").build(),
                ProductImageEntity.builder().uid("pic0").tsSize(ImageSizes.SMALL).position(0).link("link0_s").build(),
                ProductImageEntity.builder().uid("pic0").tsSize(ImageSizes.MEDIUM).position(0).link("link0_m").build(),
                ProductImageEntity.builder().uid("pic0").tsSize(ImageSizes.THUMBNAIL).position(0).link("link0_t").build(),

                ProductImageEntity.builder().uid("pic1").tsSize(ImageSizes.LARGE).position(1).link("link1_s").build(),
                ProductImageEntity.builder().uid("pic1").tsSize(ImageSizes.SMALL).position(1).link("link1_s").build(),
                ProductImageEntity.builder().uid("pic1").tsSize(ImageSizes.MEDIUM).position(1).link("link1_m").build(),
                ProductImageEntity.builder().uid("pic1").tsSize(ImageSizes.THUMBNAIL).position(1).link("link1_t").build()
            );

            ProductImageResponse expected = new ProductImageResponse(
                    allImages.stream().map(img -> new ProductImageResponse.Image(
                            img.getUid(), img.getLink(), img.getTsSize(), img.getPosition()
                    )).toList()
            );

            when(imageRepository.findByProductId(1L)).thenReturn(allImages);
            when(imageMapper.toResponseDto(any())).thenReturn(expected);

            ProductImageResponse actual = imagesService.changesPosition(1L, jsonImages);

            assertEquals(expected, actual);
            verify(imageRepository).findByProductId(1L);
            verify(imageMapper).toResponseDto(anyList());
        }

        @Test
        void validateImageCount_WhenSizeInvalid_ThrowsException() {
            ProductImageUploadRequest jsonImages = new ProductImageUploadRequest(
                    List.of(new ProductImageUploadRequest.Image("pic0", 2)));

            BadRequestException ex = assertThrows(BadRequestException.class, () ->
                    imagesService.changesPosition(1L, jsonImages));

            assertEquals(ProductImageResponseMessages.ERROR_SIZE, ex.getMessage());
        }

        @Test
        void notFoundNameUidInDataBase_ThrowsException() {
            ProductImageUploadRequest jsonImages = new ProductImageUploadRequest(
                    List.of(new ProductImageUploadRequest.Image("missingUid", 1),
                            new ProductImageUploadRequest.Image("pic1", 0))
            );

            List<ProductImageEntity> allImages = List.of(
                ProductImageEntity.builder().uid("pic0").tsSize(ImageSizes.LARGE).position(0).link("link0_s").build(),
                ProductImageEntity.builder().uid("pic0").tsSize(ImageSizes.SMALL).position(0).link("link0_s").build(),
                ProductImageEntity.builder().uid("pic0").tsSize(ImageSizes.MEDIUM).position(0).link("link0_m").build(),
                ProductImageEntity.builder().uid("pic0").tsSize(ImageSizes.THUMBNAIL).position(0).link("link0_t").build(),

                ProductImageEntity.builder().uid("pic1").tsSize(ImageSizes.LARGE).position(1).link("link1_s").build(),
                ProductImageEntity.builder().uid("pic1").tsSize(ImageSizes.SMALL).position(1).link("link1_s").build(),
                ProductImageEntity.builder().uid("pic1").tsSize(ImageSizes.MEDIUM).position(1).link("link1_m").build(),
                ProductImageEntity.builder().uid("pic1").tsSize(ImageSizes.THUMBNAIL).position(1).link("link1_t").build()
            );

            when(imageRepository.findByProductId(1L)).thenReturn(allImages);

            BadRequestException ex = assertThrows(BadRequestException.class, () ->
                    imagesService.changesPosition(1L, jsonImages));

            assertEquals(String.format(
                    ProductImageResponseMessages.ERROR_MISSING_UID,
                    List.of(jsonImages.images().get(0).uid())
            ), ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Tests remove product images")
    class DeleteImages {

        @Test
        void deleteAllImagesSuccess() throws IOException {
            List<Integer> emptyList = new ArrayList<>();
            when(productService.findProductById(1L)).thenReturn(product);
            when(imageRepository.findByProductId(1L)).thenReturn(imagesList);

            imagesService.deleteProductImages(1L, emptyList);

            verify(storageService).deleteByUrl("test link 1");
            verify(storageService).deleteByUrl("test link 2");
            verify(imageRepository).deleteAll(imagesList);
        }

        @Test
        void deletePositionImagesSuccess() throws IOException {
            List<Integer> positions = List.of(1);

            when(productService.findProductById(1L)).thenReturn(product);
            when(imageRepository.findByProductId(1L)).thenReturn(imagesList);

            imagesService.deleteProductImages(1L, positions);

            verify(storageService).deleteByUrl("test link 2");
            verify(imageRepository).deleteAll(List.of(imagesList.get(1)));
        }
    }
}

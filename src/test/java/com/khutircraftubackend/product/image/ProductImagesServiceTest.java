//package com.khutircraftubackend.product.image;
//
//import com.khutircraftubackend.exception.httpstatus.BadRequestException;
//import com.khutircraftubackend.exception.httpstatus.NotFoundException;
//import com.khutircraftubackend.product.ProductEntity;
//import com.khutircraftubackend.product.ProductService;
//import com.khutircraftubackend.product.image.responce.ProductImagesResponse;
//import com.khutircraftubackend.product.image.request.ProductImagesChanges;
//import com.khutircraftubackend.storage.StorageService;
//import com.khutircraftubackend.validated.ImageMimeValidator;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class ProductImagesServiceTest {
//
//    @Mock
//    private ProductImagesRepository imageRepository;
//    @Mock
//    private ProductService productService;
//    @InjectMocks
//    private ProductImagesService imagesService;
//    @Mock
//    private ProductImagesMapper imageMapper;
//    @Mock
//    private ProductImagesResponse responseDto;
//    @Mock
//    private StorageService storageService;
//    @Mock
//    private ImageMimeValidator mimeValidator;
//    private ProductEntity product;
//    private List<ProductImagesEntity> imagesList;
//    private static final Long MAX_COUNT_FILES = 5L;
//
//
//    @BeforeEach
//    void setup() {
//        product = ProductEntity.builder()
//                .id(1L)
//                .name("Test Product")
//                .build();
//
//        ProductImagesEntity image1 = ProductImagesEntity.builder()
//                .id(1L)
//                .link("test link 1")
//                .position(0)
//                .uid("pic0.jpeg")
//                .tsSize(ImageSizes.LARGE)
//                .product(product)
//                .build();
//
//        ProductImagesEntity image2 = ProductImagesEntity.builder()
//                .id(2L)
//                .link("test link 2")
//                .position(1)
//                .uid("pic1.jpeg")
//                .tsSize(ImageSizes.LARGE)
//                .product(product)
//                .build();
//
//        imagesList = List.of(image1, image2);
//    }
//
//    @Nested
//    @DisplayName("Product image viewing tests")
//    class ViewImages {
//
//        @Test
//        void productImagesViewSuccess() {
//            when(productService.findProductById(1L)).thenReturn(product);
//            when(imageRepository.findByProductId(1L)).thenReturn(imagesList);
//            when(imageMapper.toResponseDto(imagesList)).thenReturn(responseDto);
//
//            ProductImagesResponse actual = imagesService.getProductImages(1L);
//
//            assertEquals(responseDto, actual);
//            verify(productService).findProductById(1L);
//            verify(imageRepository).findByProductId(1L);
//            verify(imageMapper).toResponseDto(imagesList);
//        }
//
//        @Test
//        void shouldThrowNotFoundWhenProductDoesNotExist() {
//            when(productService.findProductById(2L))
//                    .thenThrow(new NotFoundException("Product with id 2 not found"));
//
//            NotFoundException ex = assertThrows(NotFoundException.class, () -> imagesService.getProductImages(2L));
//
//            assertEquals("Product with id 2 not found", ex.getMessage());
//        }
//    }
//
//    @Nested
//    @DisplayName("Product image loading Tests")
//    class UploadImages {
//
//        @Test
//        void uploadImagesSuccess() {
//            doNothing().when(mimeValidator).validateMimeTypes(anyList(), anyString());
//            ProductImagesChanges jsonImages = new ProductImagesChanges(
//                    List.of(new ProductImagesChanges.Images("pic2.jpeg", 2))
//            );
//
//            MultipartFile files = mock(MultipartFile.class);
//            when(files.getOriginalFilename()).thenReturn("pic2.jpeg");
//            List<MultipartFile> fileImages = List.of(files);
//
//            ProductImagesEntity savedEntity = ProductImagesEntity.builder()
//                    .id(3L)
//                    .uid("pic2.jpeg")
//                    .position(2)
//                    .tsSize(ImageSizes.LARGE)
//                    .link("test-link 3")
//                    .build();
//
//            List<ProductImagesEntity> savedEntities = new ArrayList<>(imagesList);
//            savedEntities.add(savedEntity);
//
//            ProductImagesResponse expected = new ProductImagesResponse(
//                    savedEntities.stream().map(img -> new ProductImagesResponse.ImageResponse(
//                            img.getUid(), img.getLink(), img.getTsSize(), img.getPosition()
//                    )).toList()
//            );
//
//            when(productService.findProductById(1L)).thenReturn(product);
//            when(imageRepository.saveAll(anyList())).thenReturn(savedEntities);
//            when(imageMapper.toResponseDto(savedEntities)).thenReturn(expected);
//
//            ProductImagesResponse actual = imagesService.uploadImages(1L, jsonImages, fileImages);
//
//            assertEquals(expected, actual);
//            verify(productService).findProductById(1L);
//            verify(imageRepository).saveAll(anyList());
//            verify(imageMapper).toResponseDto(anyList());
//        }
//
//        @Test
//        void upload_WhenFilesExceedMaxCount_ThrowsBadRequestException() {
//            doNothing().when(mimeValidator).validateMimeTypes(anyList(), anyString());
//            ProductImagesChanges jsonImages = new ProductImagesChanges(
//                    List.of(new ProductImagesChanges.Images("pic2.jpeg", 2))
//            );
//
//            List<MultipartFile> fileImages = new ArrayList<>();
//            for (int i = 0; i < 6; i++) {
//                MultipartFile files = mock(MultipartFile.class);
//                fileImages.add(files);
//            }
//
//            BadRequestException ex = assertThrows(BadRequestException.class, () ->
//                    imagesService.uploadImages(1L, jsonImages, fileImages));
//
//            assertEquals(String.format(
//                    ProductImagesResponseMessages.ERROR_TOO_MANY_IMAGES, MAX_COUNT_FILES), ex.getMessage());
//        }
//
//        @Test
//        void validateNoDuplicates_WhenPositionAlreadyExistsInRequest_ThrowsException() {
//            doNothing().when(mimeValidator).validateMimeTypes(anyList(), anyString());
//            ProductImagesChanges jsonImages = new ProductImagesChanges(
//                    List.of(new ProductImagesChanges.Images("pic2.jpeg", 1)));
//
//            MultipartFile files = mock(MultipartFile.class);
//            List<MultipartFile> fileImages = List.of(files);
//
//            when(imageRepository.findByProductId(1L)).thenReturn(imagesList);
//
//            BadRequestException ex = assertThrows(BadRequestException.class, () ->
//                    imagesService.uploadImages(1L, jsonImages, fileImages));
//
//            assertEquals(ProductImagesResponseMessages.ERROR_POSITION_EXIST, ex.getMessage());
//        }
//
//        @Test
//        void validateNoDuplicates_WhenUidAlreadyExists_ThrowsException() {
//            doNothing().when(mimeValidator).validateMimeTypes(anyList(), anyString());
//            ProductImagesChanges jsonImages = new ProductImagesChanges(
//                    List.of(new ProductImagesChanges.Images("pic1.jpeg", 2)));
//
//            MultipartFile files = mock(MultipartFile.class);
//            List<MultipartFile> fileImages = List.of(files);
//
//            when(imageRepository.findByProductId(1L)).thenReturn(imagesList);
//
//            BadRequestException ex = assertThrows(BadRequestException.class, () ->
//                    imagesService.uploadImages(1L, jsonImages, fileImages));
//
//            assertEquals(ProductImagesResponseMessages.ERROR_UID_EXIST, ex.getMessage());
//        }
//
//        @Test
//        void uploadImages_WhenFileUidNotFoundInRequest_ThrowsException() {
//            doNothing().when(mimeValidator).validateMimeTypes(anyList(), anyString());
//            ProductImagesChanges jsonImages = new ProductImagesChanges(
//                    List.of(new ProductImagesChanges.Images("pic2.jpeg", 2))
//            );
//
//            MultipartFile files = mock(MultipartFile.class);
//            List<MultipartFile> fileImages = List.of(files);
//
//            BadRequestException ex = assertThrows(BadRequestException.class, () ->
//                    imagesService.uploadImages(1L, jsonImages, fileImages));
//
//            assertEquals(String.format(ProductImagesResponseMessages.ERROR_NAME_NOT_FOUND,
//                    jsonImages.images().get(0).uid()), ex.getMessage());
//        }
//    }
//
//    @Nested
//    @DisplayName("Tests for changes in product images")
//    class ChangesImages {
//
//        @Test
//        void changesImagesSuccess() {
//            ProductImagesChanges jsonImages = new ProductImagesChanges(
//                    List.of(new ProductImagesChanges.Images("pic0", 1),
//                            new ProductImagesChanges.Images("pic1", 0))
//            );
//
//            List<ProductImagesEntity> allImages = List.of(
//                ProductImagesEntity.builder().uid("pic0").tsSize(ImageSizes.LARGE).position(0).link("link0_s").build(),
//                ProductImagesEntity.builder().uid("pic0").tsSize(ImageSizes.SMALL).position(0).link("link0_s").build(),
//                ProductImagesEntity.builder().uid("pic0").tsSize(ImageSizes.MEDIUM).position(0).link("link0_m").build(),
//                ProductImagesEntity.builder().uid("pic0").tsSize(ImageSizes.THUMBNAIL).position(0).link("link0_t").build(),
//
//                ProductImagesEntity.builder().uid("pic1").tsSize(ImageSizes.LARGE).position(1).link("link1_s").build(),
//                ProductImagesEntity.builder().uid("pic1").tsSize(ImageSizes.SMALL).position(1).link("link1_s").build(),
//                ProductImagesEntity.builder().uid("pic1").tsSize(ImageSizes.MEDIUM).position(1).link("link1_m").build(),
//                ProductImagesEntity.builder().uid("pic1").tsSize(ImageSizes.THUMBNAIL).position(1).link("link1_t").build()
//            );
//
//            ProductImagesResponse expected = new ProductImagesResponse(
//                    allImages.stream().map(img -> new ProductImagesResponse.ImageResponse(
//                            img.getUid(), img.getLink(), img.getTsSize(), img.getPosition()
//                    )).toList()
//            );
//
//            when(imageRepository.findByProductId(1L)).thenReturn(allImages);
//            when(imageMapper.toResponseDto(any())).thenReturn(expected);
//
//            ProductImagesResponse actual = imagesService.changesPosition(1L, jsonImages);
//
//            assertEquals(expected, actual);
//            verify(imageRepository).findByProductId(1L);
//            verify(imageMapper).toResponseDto(anyList());
//        }
//
//        @Test
//        void validateImageCount_WhenSizeInvalid_ThrowsException() {
//            ProductImagesChanges jsonImages = new ProductImagesChanges(
//                    List.of(new ProductImagesChanges.Images("pic0", 2)));
//
//            BadRequestException ex = assertThrows(BadRequestException.class, () ->
//                    imagesService.changesPosition(1L, jsonImages));
//
//            assertEquals(ProductImagesResponseMessages.ERROR_SIZE, ex.getMessage());
//        }
//
//        @Test
//        void notFoundNameUidInDataBase_ThrowsException() {
//            ProductImagesChanges jsonImages = new ProductImagesChanges(
//                    List.of(new ProductImagesChanges.Images("missingUid", 1),
//                            new ProductImagesChanges.Images("pic1", 0))
//            );
//
//            List<ProductImagesEntity> allImages = List.of(
//                ProductImagesEntity.builder().uid("pic0").tsSize(ImageSizes.LARGE).position(0).link("link0_s").build(),
//                ProductImagesEntity.builder().uid("pic0").tsSize(ImageSizes.SMALL).position(0).link("link0_s").build(),
//                ProductImagesEntity.builder().uid("pic0").tsSize(ImageSizes.MEDIUM).position(0).link("link0_m").build(),
//                ProductImagesEntity.builder().uid("pic0").tsSize(ImageSizes.THUMBNAIL).position(0).link("link0_t").build(),
//
//                ProductImagesEntity.builder().uid("pic1").tsSize(ImageSizes.LARGE).position(1).link("link1_s").build(),
//                ProductImagesEntity.builder().uid("pic1").tsSize(ImageSizes.SMALL).position(1).link("link1_s").build(),
//                ProductImagesEntity.builder().uid("pic1").tsSize(ImageSizes.MEDIUM).position(1).link("link1_m").build(),
//                ProductImagesEntity.builder().uid("pic1").tsSize(ImageSizes.THUMBNAIL).position(1).link("link1_t").build()
//            );
//
//            when(imageRepository.findByProductId(1L)).thenReturn(allImages);
//
//            BadRequestException ex = assertThrows(BadRequestException.class, () ->
//                    imagesService.changesPosition(1L, jsonImages));
//
//            assertEquals(String.format(
//                    ProductImagesResponseMessages.ERROR_MISSING_UID,
//                    List.of(jsonImages.images().get(0).uid())
//            ), ex.getMessage());
//        }
//    }
//
//    @Nested
//    @DisplayName("Tests remove product images")
//    class DeleteImages {
//
//        @Test
//        void deleteAllImagesSuccess() throws IOException {
//            List<Integer> emptyList = new ArrayList<>();
//            when(productService.findProductById(1L)).thenReturn(product);
//            when(imageRepository.findByProductId(1L)).thenReturn(imagesList);
//
//            imagesService.deleteProductImages(1L, emptyList);
//
//            verify(storageService).deleteByUrl("test link 1");
//            verify(storageService).deleteByUrl("test link 2");
//            verify(imageRepository).deleteAll(imagesList);
//        }
//
//        @Test
//        void deletePositionImagesSuccess() throws IOException {
//            List<Integer> positions = List.of(1);
//
//            when(productService.findProductById(1L)).thenReturn(product);
//            when(imageRepository.findByProductId(1L)).thenReturn(imagesList);
//
//            imagesService.deleteProductImages(1L, positions);
//
//            verify(storageService).deleteByUrl("test link 2");
//            verify(imageRepository).deleteAll(List.of(imagesList.get(1)));
//        }
//    }
//}

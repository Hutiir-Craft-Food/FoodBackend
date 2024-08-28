package com.khutircraftubackend.product;

import com.khutircraftubackend.exception.product.ProductNotFoundException;
import com.khutircraftubackend.product.image.FileConverterService;
import com.khutircraftubackend.product.image.FileUploadService;
import com.khutircraftubackend.product.request.ProductCreateRequest;
import com.khutircraftubackend.product.request.ProductUpdateRequest;
import com.khutircraftubackend.seller.SellerEntity;
import com.khutircraftubackend.seller.SellerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private SellerService sellerService;
    @InjectMocks
    private ProductService productService;
    private SellerEntity seller;
    private ProductEntity product;
    @Mock
    private FileConverterService fileConverterService;

    @Mock
    private FileUploadService fileUploadService;

    @BeforeEach
    void setUp() {
        seller = new SellerEntity();
        seller.setCompanyName("Test company");
        seller.setId(1L);

        product = new ProductEntity();
        product.setId(1L);
        product.setName("Test product");
        product.setSeller(seller);
    }

    @Test
    void testCanModifyProduct_ProductExistsAndBelongsToCurrentSeller() {
        when(productRepository.findProductById(1L)).thenReturn(Optional.of(product));
        when(sellerService.getCurrentSeller()).thenReturn(seller);

        boolean canModify = productService.canModifyProduct(1L);

        assertTrue(canModify);
    }

    @Test
    void testCanModifyProduct_ProductNotFound() {
        when(productRepository.findProductById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.canModifyProduct(1L));
    }

    @Test
    void testCreateProduct_Success() throws IOException {
        SellerEntity currentSeller = SellerEntity.builder()
                .companyName("CompanyA")
                .build();

        SellerEntity requestSeller = SellerEntity.builder()
                .companyName("CompanyA")
                .build();

        Long sellerId = 1L;

        MultipartFile mockThumbnailFile = new MockMultipartFile("thumbnail", "test-thumbnail.jpg", "image/jpeg", "Test thumbnail content".getBytes());
        MultipartFile mockImageFile = new MockMultipartFile("image", "test-image.jpg", "image/jpeg", "Test image content".getBytes());

        when(sellerService.getSellerId(sellerId)).thenReturn(requestSeller);
        when(sellerService.getCurrentSeller()).thenReturn(currentSeller);
        when(productRepository.save(any(ProductEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(fileConverterService.convert(mockThumbnailFile)).thenReturn("uploaded-thumbnail-url");
        when(fileConverterService.convert(mockImageFile)).thenReturn("uploaded-image-url");

        ProductEntity createdProduct = productService.createProduct(
                "Test product",
                mockThumbnailFile,
                mockImageFile,
                true,
                "Test description",
                sellerId
        );

        assertNotNull(createdProduct, "Created product should not be null");
        assertEquals("Test product", createdProduct.getName());
        assertEquals("uploaded-thumbnail-url", createdProduct.getThumbnailImageUrl());
        assertEquals("uploaded-image-url", createdProduct.getImageUrl());

        verify(fileConverterService).convert(mockThumbnailFile);
        verify(fileConverterService).convert(mockImageFile);
        verify(productRepository).save(any(ProductEntity.class));
        verify(sellerService).getSellerId(sellerId);
    }

    @Test
    void testCreateProduct_AccessDenied() {
        SellerEntity currentSeller = SellerEntity.builder()
                .companyName("CompanyA")
                .build();

        SellerEntity requestSeller = SellerEntity.builder()
                .companyName("CompanyB")
                .build();

        ProductCreateRequest request = ProductCreateRequest.builder()
                .name("Test product")
                .description("Test description")
                .seller(requestSeller)
                .build();

        Long sellerId = 2L;

        when(sellerService.getCurrentSeller()).thenReturn(currentSeller);
        when(sellerService.getSellerId(sellerId)).thenReturn(requestSeller);

        assertThrows(AccessDeniedException.class, () ->
                productService.createProduct(
                        "Test product",
                        null,
                        null,
                        true,
                        "Test description",
                        sellerId
                )
        );
        verify(productRepository, never()).save(any(ProductEntity.class));
    }

    @Test
    void testPatchProduct_Success() throws IOException {
        ProductEntity existingProduct = ProductEntity.builder()
                .id(1L)
                .name("Old name")
                .thumbnailImageUrl("Old thumbnail")
                .imageUrl("Old image")
                .available(false)
                .description("Old description")
                .build();

        MultipartFile mockImageFile = new MockMultipartFile("image", "updated-image.jpg", "image/jpeg", "Updated image content".getBytes());
        MultipartFile mockThumbnailFile = new MockMultipartFile("thumbnail", "updated-thumbnail.jpg", "image/jpeg", "Updated thumbnail content".getBytes());

        ProductUpdateRequest request = ProductUpdateRequest.builder()
                .name("Updated name")
                .thumbnailImage(mockThumbnailFile)
                .image(mockImageFile)
                .available(true)
                .description("Updated description")
                .build();

        when(productRepository.findProductById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(ProductEntity.class))).thenReturn(existingProduct);

        when(fileConverterService.convert(mockImageFile)).thenReturn("new-uploaded-image-url");
        when(fileConverterService.convert(mockThumbnailFile)).thenReturn("new-uploaded-thumbnail-url");

        ProductEntity updatedProduct = productService.patchProduct(1L, request, mockThumbnailFile, mockImageFile);

        assertNotNull(updatedProduct);
        assertEquals("Updated name", updatedProduct.getName());
        assertEquals("new-uploaded-image-url", updatedProduct.getImageUrl());
        assertEquals("new-uploaded-thumbnail-url", updatedProduct.getThumbnailImageUrl());
        assertTrue(updatedProduct.isAvailable());
        assertEquals("Updated description", updatedProduct.getDescription());

        verify(productRepository, times(1)).save(any(ProductEntity.class));
        verify(fileConverterService, times(1)).convert(mockImageFile);
        verify(fileConverterService, times(1)).convert(mockThumbnailFile);
    }

    @Test
    void testPatchProduct_WithNullFields() throws IOException {
        ProductUpdateRequest request = ProductUpdateRequest.builder()
                .name("Updated name")
                .description(null)
                .build();

        when(productRepository.findProductById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(ProductEntity.class))).thenReturn(product);

        ProductEntity patchedProduct = productService.patchProduct(1L, request, null, null);

        assertEquals("Updated name", patchedProduct.getName());
        assertEquals(product.getDescription(), patchedProduct.getDescription());
        assertEquals(product.getImageUrl(), patchedProduct.getImageUrl());
        assertEquals(product.getThumbnailImageUrl(), patchedProduct.getThumbnailImageUrl());

        verify(productRepository, times(1)).save(any(ProductEntity.class));

    }

    @Test
    void testPatchProduct_WithMissingFields() throws IOException {
        ProductUpdateRequest request = ProductUpdateRequest.builder()
                .name("New name")
                .build();

        ProductEntity originalProduct = new ProductEntity();
        originalProduct.setName("Old name");
        originalProduct.setDescription("Old description");
        originalProduct.setImageUrl("Old image");
        originalProduct.setThumbnailImageUrl("Old thumbnail");

        when(productRepository.findProductById(1L)).thenReturn(Optional.of(originalProduct));
        when(productRepository.save(any(ProductEntity.class))).thenReturn(originalProduct);

        ProductEntity patchedProduct = productService.patchProduct(1L, request, null, null);

        assertEquals("New name", patchedProduct.getName());
        assertEquals("Old description", patchedProduct.getDescription());
        assertEquals("Old image", patchedProduct.getImageUrl());
        assertEquals("Old thumbnail", patchedProduct.getThumbnailImageUrl());
    }

    @Test
    void testUpdateProduct_ProductNotFound() {
        when(productRepository.findProductById(1L)).thenReturn(Optional.empty());

        ProductUpdateRequest request = ProductUpdateRequest.builder()
                .name("Test product")
                .build();

        assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(1L, request, null, null));
    }

    @Test
    void testDeleteProduct_ProductNotFound() {
        when(productRepository.findProductById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(1L));
        verify(productRepository, never()).delete(any(ProductEntity.class));
    }

    @Test
    void testDeleteProduct_Success() throws IOException {
        ProductEntity product = new ProductEntity();
        product.setImageUrl("Test image");
        product.setThumbnailImageUrl("Test thumbnail");

        when(productRepository.findProductById(1L)).thenReturn(Optional.of(product));

        doNothing().when(fileUploadService).deleteCloudinaryById("Test image");
        doNothing().when(fileUploadService).deleteCloudinaryById("Test thumbnail");

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).delete(product);
        verify(fileUploadService, times(1)).deleteCloudinaryById("Test image");
        verify(fileUploadService, times(1)).deleteCloudinaryById("Test thumbnail");
    }

    @Test
    void testDeleteAllProductsForCurrentSeller() throws IOException {
        when(sellerService.getCurrentSeller()).thenReturn(seller);

        productService.deleteAllProductsForCurrentSeller();

        verify(productRepository, times(1)).deleteBySeller(seller);
    }

    @Test
    void testGetProducts_Success() {
        when(productRepository.findAllBy(any(Pageable.class))).thenReturn(List.of(product));

        List<ProductEntity> products = productService.getProducts(0, 10);

        assertEquals(1, products.size());
        assertEquals("Test product", products.get(0).getName());
        verify(productRepository, times(1)).findAllBy(any(Pageable.class));
    }

}

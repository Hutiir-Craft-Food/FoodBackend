package com.khutircraftubackend.product;

import com.khutircraftubackend.category.CategoryEntity;
import com.khutircraftubackend.category.CategoryService;
import com.khutircraftubackend.category.exception.category.CategoryNotFoundException;
import com.khutircraftubackend.product.exception.product.ProductNotFoundException;
import com.khutircraftubackend.product.image.FileConverterService;
import com.khutircraftubackend.product.image.FileUploadService;
import com.khutircraftubackend.product.request.ProductRequest;
import com.khutircraftubackend.seller.SellerEntity;
import com.khutircraftubackend.seller.SellerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
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
	@Mock
	private FileConverterService fileConverterService;
	@Mock
	private FileUploadService fileUploadService;
	@Mock
	private MultipartFile mockThumbnailFile;
	@Mock
	private MultipartFile mockImageFile;
	@Mock
	private CategoryService categoryService;
	@Spy
	private ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);
	@InjectMocks
	private ProductService productService;
	
	private SellerEntity seller;
	private ProductEntity product;
	
	@BeforeEach
	void setUp() {
		
		seller = new SellerEntity();
		seller.setCompanyName("Test company");
		seller.setId(1L);
		
		product = new ProductEntity();
		product.setId(1L);
		product.setName("Test product");
		product.setSeller(seller);
		
		mockThumbnailFile = new MockMultipartFile("thumbnail", "test-thumbnail.jpg", "image/jpeg", "Test thumbnail content".getBytes());
		mockImageFile = new MockMultipartFile("image", "test-image.jpg", "image/jpeg", "Test image content".getBytes());
		
	}
	
	@Test
	void getProducts_Success() {
		
		when(productRepository.findAllBy(any(Pageable.class))).thenReturn(List.of(product));
		
		List<ProductEntity> products = productService.getProducts(0, 10);
		
		assertEquals(1, products.size());
		assertEquals("Test product", products.get(0).getName());
		verify(productRepository, times(1)).findAllBy(any(Pageable.class));
	}
	
	@Nested
	@DisplayName("Tests for modify product")
	class CanModify {
		@Test
		public void canModifyProduct_Success() throws AccessDeniedException {
			
			when(sellerService.getCurrentSeller()).thenReturn(seller);
			when(productRepository.findProductById(1L)).thenReturn(Optional.of(product));
			
			boolean canModify = productService.canModifyProduct(1L);
			
			assertTrue(canModify);
		}
		
		@Test
		public void canModifyProduct_Failure() {
			
			SellerEntity otherSeller = new SellerEntity();
			otherSeller.setId(2L);
			otherSeller.setCompanyName("Company B");
			
			when(sellerService.getCurrentSeller()).thenReturn(otherSeller);
			when(productRepository.findProductById(1L)).thenReturn(Optional.of(product));
			
			assertThrows(AccessDeniedException.class, () -> productService.canModifyProduct(1L));
		}
		
		@Test
		void canModifyProduct_ProductExistsAndBelongsToCurrentSeller() throws AccessDeniedException {
			
			when(productRepository.findProductById(1L)).thenReturn(Optional.of(product));
			when(sellerService.getCurrentSeller()).thenReturn(seller);
			
			boolean canModify = productService.canModifyProduct(1L);
			
			assertTrue(canModify);
		}
		
		@Test
		void canModifyProduct_ProductNotFound() {
			
			when(productRepository.findProductById(1L)).thenReturn(Optional.empty());
			
			assertThrows(ProductNotFoundException.class, () -> productService.canModifyProduct(1L));
		}
		
	}
	
	@Nested
	@DisplayName("Tests for creation Product")
	class CreateProduct {
		@Test
		void createProduct_Success() throws IOException {
			
			SellerEntity currentSeller = SellerEntity.builder()
					.companyName("CompanyA")
					.build();
			
			Long categoryId = 2L;
			
			when(sellerService.getCurrentSeller()).thenReturn(currentSeller);
			when(productRepository.save(any(ProductEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
			when(fileConverterService.convert(mockThumbnailFile)).thenReturn("uploaded-thumbnail-url");
			when(fileConverterService.convert(mockImageFile)).thenReturn("uploaded-image-url");
			
			CategoryEntity mockCategory = CategoryEntity.builder()
					.id(categoryId)
					.name("Test Category")
					.build();
			when(categoryService.findCategoryById(anyLong())).thenReturn(mockCategory);
			
			ProductRequest request = ProductRequest.builder()
					.name("Test product")
					.available(true)
					.description("Test description")
					.categoryId(categoryId)
					.build();
			
			ProductEntity createdProduct = productService.createProduct(request, mockThumbnailFile, mockImageFile);
			
			assertNotNull(createdProduct, "Created product should not be null");
			assertEquals("Test product", createdProduct.getName());
			assertEquals("uploaded-thumbnail-url", createdProduct.getThumbnailImageUrl());
			assertEquals("uploaded-image-url", createdProduct.getImageUrl());
			
			verify(fileConverterService).convert(mockThumbnailFile);
			verify(fileConverterService).convert(mockImageFile);
			verify(productRepository).save(any(ProductEntity.class));
			verify(categoryService).findCategoryById(anyLong());
		}
		
		@Test
		void createProduct_CategoryNotFound() {
			
			ProductRequest request = ProductRequest.builder()
					.name("Test product")
					.description("Test description")
					.available(true)
					.categoryId(2L)
					.build();
			
			when(categoryService.findCategoryById(2L)).thenThrow(new CategoryNotFoundException("Category not found"));
			
			assertThrows(CategoryNotFoundException.class, () ->
					productService.createProduct(request, mockThumbnailFile, mockImageFile));
		}
		
		@Test
		void testUpdateProduct_Success() throws IOException {
			
			ProductEntity existingProduct = ProductEntity.builder()
					.id(1L)
					.name("Old name")
					.available(false)
					.description("Old description")
					.build();
			
			ProductRequest request = ProductRequest.builder()
					.name("Updated name")
					.available(true)
					.description("Updated description")
					.categoryId(2L)
					.build();
			
			when(productRepository.findProductById(1L)).thenReturn(Optional.of(existingProduct));
			when(fileConverterService.convert(mockImageFile)).thenReturn("new-uploaded-image-url");
			when(fileConverterService.convert(mockThumbnailFile)).thenReturn("new-uploaded-thumbnail-url");
			when(productRepository.save(any(ProductEntity.class))).thenReturn(existingProduct);
			
			ProductEntity updatedProduct = productService.updateProduct(1L, request, mockThumbnailFile, mockImageFile);
			
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
		
	}
	
	@Nested
	@DisplayName("Tests for update Product")
	class UpdateProduct {
		@Test
		void updateProduct_WithNullImages() throws IOException {
			
			ProductEntity existingProduct = ProductEntity.builder()
					.id(1L)
					.name("Existing Product")
					.available(true)
					.description("Existing Description")
					.imageUrl("existing-image-url")
					.thumbnailImageUrl("existing-thumbnail-url")
					.build();
			
			ProductRequest request = ProductRequest.builder()
					.name("Updated Product")
					.available(true)
					.description("Updated Description")
					.categoryId(2L)
					.build();
			
			
			when(productRepository.findProductById(1L)).thenReturn(Optional.of(existingProduct));
			when(productRepository.save(any(ProductEntity.class))).thenReturn(existingProduct);
			
			ProductEntity updatedProduct = productService.updateProduct(1L, request, null, null);
			
			assertNotNull(updatedProduct, "Updated product should not be null");
			assertEquals("Updated Product", updatedProduct.getName());
			assertEquals("Updated Description", updatedProduct.getDescription());
			
			assertNull(updatedProduct.getImageUrl(), "Image URL should be null when no image is provided");
			assertNull(updatedProduct.getThumbnailImageUrl(), "Thumbnail URL should be null when no thumbnail is provided");
			
			verify(productRepository, times(1)).save(any(ProductEntity.class));
		}
		
		@Test
		void updateProduct_ProductNotFound() {
			
			when(productRepository.findProductById(1L)).thenReturn(Optional.empty());
			
			ProductRequest request = ProductRequest.builder()
					.name("Test product")
					.build();
			
			assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(1L, request, null, null));
		}
		
		
	}
	
	@Nested
	@DisplayName("Tests for delete Product")
	class DeleteProduct {
		
		@Test
		void deleteProduct_ProductNotFound() {
			
			when(productRepository.findProductById(1L)).thenReturn(Optional.empty());
			
			assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(1L));
			verify(productRepository, never()).delete(any(ProductEntity.class));
		}
		
		@Test
		void deleteProduct_Success() throws IOException {
			
			String imageUrl = "http://Test image";
			String thumbnailUrl = "http://Test thumbnail";
			
			ProductEntity product = new ProductEntity();
			product.setImageUrl(imageUrl);
			product.setThumbnailImageUrl(thumbnailUrl);
			
			when(productRepository.findProductById(1L)).thenReturn(Optional.of(product));
			
			doNothing().when(fileUploadService).deleteCloudinaryById(anyString());
			when(fileUploadService.extractPublicId(imageUrl)).thenReturn("Test image");
			when(fileUploadService.extractPublicId(thumbnailUrl)).thenReturn("Test thumbnail");
			
			productService.deleteProduct(1L);
			
			verify(productRepository, times(1)).delete(product);
			verify(fileUploadService, times(1)).deleteCloudinaryById("Test image");
			verify(fileUploadService, times(1)).deleteCloudinaryById("Test thumbnail");
		}
		
		@Test
		void deleteAllProductsForSeller_ShouldDeleteAllProductsAndImagesForSeller() throws IOException {
			
			SellerEntity seller = new SellerEntity();
			ProductEntity product1 = new ProductEntity();
			product1.setThumbnailImageUrl("http://Test thumbnail1");
			product1.setImageUrl("http://Test image1");
			
			ProductEntity product2 = new ProductEntity();
			product2.setThumbnailImageUrl("http://Test thumbnail2");
			product2.setImageUrl("http://Test image2");
			
			List<ProductEntity> products = List.of(product1, product2);
			
			when(productRepository.findAllBySeller(seller)).thenReturn(products);
			when(fileUploadService.extractPublicId("http://Test thumbnail1")).thenReturn("publicId1");
			when(fileUploadService.extractPublicId("http://Test image1")).thenReturn("publicId2");
			when(fileUploadService.extractPublicId("http://Test thumbnail2")).thenReturn("publicId3");
			when(fileUploadService.extractPublicId("http://Test image2")).thenReturn("publicId4");
			
			productService.deleteAllProductsForSeller(seller);
			
			verify(fileUploadService, times(1)).deleteCloudinaryById("publicId1");
			verify(fileUploadService, times(1)).deleteCloudinaryById("publicId2");
			verify(fileUploadService, times(1)).deleteCloudinaryById("publicId3");
			verify(fileUploadService, times(1)).deleteCloudinaryById("publicId4");
			
			verify(productRepository, times(1)).deleteBySeller(seller);
		}
	}
}

package com.khutircraftubackend.product;

import com.khutircraftubackend.category.CategoryEntity;
import com.khutircraftubackend.category.CategoryMapper;
import com.khutircraftubackend.category.CategoryService;
import com.khutircraftubackend.category.exception.CategoryNotFoundException;
import com.khutircraftubackend.product.exception.ProductNotFoundException;
import com.khutircraftubackend.product.request.ProductRequest;
import com.khutircraftubackend.product.response.ProductResponse;
import com.khutircraftubackend.seller.SellerEntity;
import com.khutircraftubackend.seller.SellerMapper;
import com.khutircraftubackend.seller.SellerService;
import com.khutircraftubackend.storage.StorageService;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
	
	@Mock
	private ProductRepository productRepository;

	@Mock
	private SellerService sellerService;

	@Mock
	private StorageService storageService;

	@Mock
	private CategoryService categoryService;
	
	@Spy
	private SellerMapper sellerMapper = Mappers.getMapper(SellerMapper.class);
	
	@Spy
	private CategoryMapper categoryMapper = Mappers.getMapper(CategoryMapper.class);
	
	@Spy
	@InjectMocks
	private ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

	@InjectMocks
	private ProductService productService;
	
	private SellerEntity seller;
	private ProductEntity product;
	private MultipartFile mockThumbnailFile;
	private MultipartFile mockImageFile;
	
	
	@BeforeEach
	void setUp() {
		
		seller = new SellerEntity();
		seller.setId(1L);
		seller.setSellerName("Test company");

		product = new ProductEntity();
		product.setId(1L);
		product.setName("Test product");
		product.setSeller(seller);
		
		mockThumbnailFile = new MockMultipartFile("thumbnail", "test-thumbnail.jpg", "image/jpeg", "Test thumbnail content".getBytes());
		mockImageFile = new MockMultipartFile("image", "test-image.jpg", "image/jpeg", "Test image content".getBytes());
		
	}
	
	@Test
	void getProducts_Success() {
		
		Pageable pageable = PageRequest.of(0, 10);
		
		SellerEntity sellerEntity = SellerEntity.builder()
				.id(1L)
				.sellerName("Test")
				.build();
		
		ProductEntity productEntity = ProductEntity.builder()
				.id(1L)
				.name("Test Product")
				.seller(sellerEntity)
				.build();

		lenient().when(productRepository.findAllBy(pageable)).thenReturn(new PageImpl<>(List.of(productEntity), pageable, 1));
		lenient().when(productRepository.count()).thenReturn(1L);
		
		Map<String, Object> result = productService.getProducts(0, 10);
		
		assertNotNull(result);
		assertEquals(4, result.size());
		assertEquals(1L, result.get("total"));
		assertEquals(0, result.get("offset"));
		assertEquals(10, result.get("limit"));
		
		@SuppressWarnings("unchecked")
		Collection<ProductResponse> products = (Collection<ProductResponse>) result.get("products");
		assertNotNull(products);
		assertEquals(1, products.size());
		ProductResponse response = products.iterator().next();
		assertEquals("Test Product", response.name());
		assertNotNull(response.seller());
		assertEquals("Test", response.seller().sellerName());
	}
	
	@Nested
	@DisplayName("Tests for modify product")
	class CanModify {
		@Test
		void canModifyProduct_Success() throws AccessDeniedException {
			
			when(sellerService.getCurrentSeller()).thenReturn(seller);
			when(productRepository.findProductById(1L)).thenReturn(Optional.of(product));
			
			boolean canModify = productService.canModifyProduct(1L);
			
			assertTrue(canModify);
		}
		
		@Test
		void canModifyProduct_Failure() {
			
			SellerEntity otherSeller = new SellerEntity();
			otherSeller.setId(2L);
			otherSeller.setSellerName("test");

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
					.sellerName("Test c")
					.build();
			
			Long categoryId = 2L;
			
			when(sellerService.getCurrentSeller()).thenReturn(currentSeller);
			when(storageService.upload(mockThumbnailFile)).thenReturn("ThumbnailFile");
			when(storageService.upload(mockImageFile)).thenReturn("ImageFile");
			when(productRepository.save(any(ProductEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
			
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
			
			assertNotNull(createdProduct);
			assertEquals("Test product", createdProduct.getName());
			assertEquals("ThumbnailFile", createdProduct.getThumbnailImageUrl());
			assertEquals("ImageFile", createdProduct.getImageUrl());
			
			verify(storageService).upload(mockThumbnailFile);
			verify(storageService).upload(mockImageFile);
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
			CategoryEntity mockCategory = new CategoryEntity(2L, "Updated Category", null, null, null, null);
			
			when(productRepository.findProductById(1L)).thenReturn(Optional.of(existingProduct));
			when(categoryService.findCategoryById(2L)).thenReturn(mockCategory);
			when(storageService.upload(mockImageFile)).thenReturn("new-uploaded-image-url");
			when(storageService.upload(mockThumbnailFile)).thenReturn("new-uploaded-thumbnail-url");
			when(productRepository.save(any(ProductEntity.class))).thenReturn(existingProduct);
			
			ProductEntity updatedProduct = productService.updateProduct(1L, request, mockThumbnailFile, mockImageFile);
			
			assertNotNull(updatedProduct);
			assertEquals("Updated name", updatedProduct.getName());
			assertEquals("new-uploaded-image-url", updatedProduct.getImageUrl());
			assertEquals("new-uploaded-thumbnail-url", updatedProduct.getThumbnailImageUrl());
			assertTrue(updatedProduct.isAvailable());
			assertEquals("Updated description", updatedProduct.getDescription());
			
			verify(productRepository, times(1)).save(any(ProductEntity.class));
			verify(storageService, times(1)).upload(mockImageFile);
			verify(storageService, times(1)).upload(mockThumbnailFile);
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
			
			CategoryEntity mockCategory = new CategoryEntity(2L, "Updated Category", null, null, null, null);
			
			when(productRepository.findProductById(1L)).thenReturn(Optional.of(existingProduct));
			when(categoryService.findCategoryById(2L)).thenReturn(mockCategory);
			when(productRepository.save(any(ProductEntity.class))).thenReturn(existingProduct);
			
			ProductEntity updatedProduct = productService.updateProduct(1L, request, null, null);
			
			assertNotNull(updatedProduct, "Updated product should not be null");
			assertEquals("Updated Product", updatedProduct.getName());
			assertEquals("Updated Description", updatedProduct.getDescription());
			
			assertEquals("", updatedProduct.getImageUrl());
			assertEquals("", updatedProduct.getThumbnailImageUrl());
			
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
			
			
			ProductEntity product = new ProductEntity();
			
			when(productRepository.findProductById(1L)).thenReturn(Optional.of(product));
			
			productService.deleteProduct(1L);
			
			verify(productRepository, times(1)).delete(product);
		}
		
		@Test
		void deleteAllProductsForSeller_ShouldDeleteAllProductsAndImagesForSeller() throws IOException {
			
			SellerEntity seller = new SellerEntity();
			
			ProductEntity product1 = new ProductEntity();
			product1.setSeller(seller);
			product1.setThumbnailImageUrl("http://Test thumbnail1");
			product1.setImageUrl("http://Test image1");
			
			ProductEntity product2 = new ProductEntity();
			product2.setSeller(seller);
			product2.setThumbnailImageUrl("http://Test thumbnail2");
			product2.setImageUrl("http://Test image2");
			
			List<ProductEntity> products = List.of(product1, product2);
			
			when(productRepository.findAllBySeller(seller)).thenReturn(products);
			
			doNothing().when(storageService).deleteByUrl(anyString());
			
			productService.deleteAllProductsForSeller(seller);
			
			verify(storageService, times(1)).deleteByUrl("http://Test thumbnail1");
			verify(storageService, times(1)).deleteByUrl("http://Test image1");
			verify(storageService, times(1)).deleteByUrl("http://Test thumbnail2");
			verify(storageService, times(1)).deleteByUrl("http://Test image2");
			
			verify(productRepository, times(1)).deleteBySeller(seller);
		}
	}
}

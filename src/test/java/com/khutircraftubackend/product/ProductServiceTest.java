package com.khutircraftubackend.product;

import com.khutircraftubackend.category.CategoryEntity;
import com.khutircraftubackend.category.CategoryMapper;
import com.khutircraftubackend.category.CategoryService;
import com.khutircraftubackend.category.exception.CategoryNotFoundException;
import com.khutircraftubackend.product.exception.ProductAccessException;
import com.khutircraftubackend.product.exception.ProductNotFoundException;
import com.khutircraftubackend.product.request.ProductRequest;
import com.khutircraftubackend.product.response.ProductResponse;
import com.khutircraftubackend.seller.SellerEntity;
import com.khutircraftubackend.seller.SellerMapper;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
	
	@BeforeEach
	void setUp() {

		seller = new SellerEntity();
		seller.setId(1L);
		seller.setSellerName("Test company");

		product = new ProductEntity();
		product.setId(1L);
		product.setName("Test product");
		product.setSeller(seller);
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

		verify(sellerMapper, atLeastOnce()).toSellerResponse(any());
	}
	
	@Nested
	@DisplayName("Tests for modify product")
	class CanModify {
		@Test
		void canModifyProduct_Success() {
			
			when(sellerService.getCurrentSeller()).thenReturn(seller);
			when(productRepository.findProductById(1L)).thenReturn(Optional.of(product));
			
			boolean canModify = productService.assertCanModifyProduct(1L);
			
			assertTrue(canModify);
		}
		
		@Test
		void canModifyProduct_Failure() {
			
			SellerEntity otherSeller = new SellerEntity();
			otherSeller.setId(2L);
			otherSeller.setSellerName("test");

			when(sellerService.getCurrentSeller()).thenReturn(otherSeller);
			when(productRepository.findProductById(1L)).thenReturn(Optional.of(product));
			
			assertThrows(ProductAccessException.class, () -> productService.assertCanModifyProduct(1L));
		}
		
		@Test
		void canModifyProduct_ProductExistsAndBelongsToCurrentSeller() {
			
			when(productRepository.findProductById(1L)).thenReturn(Optional.of(product));
			when(sellerService.getCurrentSeller()).thenReturn(seller);
			
			boolean canModify = productService.assertCanModifyProduct(1L);
			
			assertTrue(canModify);
		}
		
		@Test
		void canModifyProduct_ProductNotFound() {
			
			when(productRepository.findProductById(1L)).thenReturn(Optional.empty());
			
			assertThrows(ProductNotFoundException.class, () -> productService.assertCanModifyProduct(1L));
		}
		
	}
	
	@Nested
	@DisplayName("Tests for creation Product")
	class CreateProduct {
		@Test
		void createProduct_Success() {

			SellerEntity currentSeller = SellerEntity.builder()
					.sellerName("Test c")
					.build();

			Long categoryId = 2L;

			when(sellerService.getCurrentSeller()).thenReturn(currentSeller);
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

			ProductEntity createdProduct = productService.createProduct(request);

			assertNotNull(createdProduct);
			assertEquals("Test product", createdProduct.getName());

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
					productService.createProduct(request));
		}

		@Test
		void testUpdateProduct_Success() {

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
			when(productRepository.save(any(ProductEntity.class))).thenReturn(existingProduct);

			ProductEntity updatedProduct = productService.updateProduct(1L, request);

			assertNotNull(updatedProduct);
			assertEquals("Updated name", updatedProduct.getName());
			assertTrue(updatedProduct.isAvailable());
			assertEquals("Updated description", updatedProduct.getDescription());

			verify(productRepository, times(1)).save(any(ProductEntity.class));
		}

	}

	@Nested
	@DisplayName("Tests for update Product")
	class UpdateProduct {
		@Test
		void updateProduct_WithNullImages() {//TODO Need this test??

			ProductEntity existingProduct = ProductEntity.builder()
					.id(1L)
					.name("Existing Product")
					.available(true)
					.description("Existing Description")
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

			ProductEntity updatedProduct = productService.updateProduct(1L, request);

			assertNotNull(updatedProduct, "Updated product should not be null");
			assertEquals("Updated Product", updatedProduct.getName());
			assertEquals("Updated Description", updatedProduct.getDescription());

			verify(productRepository, times(1)).save(any(ProductEntity.class));
		}

		@Test
		void updateProduct_ProductNotFound() {

			when(productRepository.findProductById(1L)).thenReturn(Optional.empty());

			ProductRequest request = ProductRequest.builder()
					.name("Test product")
					.build();

			assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(1L, request));
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
		void deleteProduct_Success() {
			
			
			ProductEntity product = new ProductEntity();
			
			when(productRepository.findProductById(1L)).thenReturn(Optional.of(product));
			
			productService.deleteProduct(1L);
			
			verify(productRepository, times(1)).delete(product);
		}
		
		@Test
		void deleteAllProductsForSeller(){

			SellerEntity seller = new SellerEntity();

			ProductEntity product1 = new ProductEntity();
			product1.setSeller(seller);

			ProductEntity product2 = new ProductEntity();
			product2.setSeller(seller);

			productService.deleteAllProductsForSeller(seller);

			verify(productRepository, times(1)).deleteBySeller(seller);
		}
	}
}

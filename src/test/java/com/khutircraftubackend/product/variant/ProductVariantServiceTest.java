package com.khutircraftubackend.product.variant;

import com.khutircraftubackend.product.ProductEntity;
import com.khutircraftubackend.product.exception.InvalidPriceIdsException;
import com.khutircraftubackend.product.image.ImageSize;
import com.khutircraftubackend.product.image.ProductImageRepository;
import com.khutircraftubackend.product.image.ProductThumbnailProjection;
import com.khutircraftubackend.product.price.entity.ProductPriceEntity;
import com.khutircraftubackend.product.price.entity.ProductUnitEntity;
import com.khutircraftubackend.product.price.mapper.ProductVariantMapper;
import com.khutircraftubackend.product.price.repo.ProductPriceRepository;
import com.khutircraftubackend.product.price.response.ProductVariantResponse;
import com.khutircraftubackend.product.price.service.ProductVariantService;
import com.khutircraftubackend.seller.SellerEntity;
import com.khutircraftubackend.seller.SellerResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static com.khutircraftubackend.product.exception.ProductResponseMessage.AT_LEAST_ONE_PRICE_ID_REQUIRED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductVariantServiceTest {

	@Mock
	private ProductPriceRepository productPriceRepository;

	@Mock
	private ProductImageRepository productImageRepository;

	@Mock
	private ProductVariantMapper productVariantMapper;

	@InjectMocks
	private ProductVariantService productVariantService;

	private ProductEntity createProduct(Long id, String name, boolean available) {

		SellerEntity seller = SellerEntity.builder()
				.id(id + 100)
				.sellerName("Seller " + id)
				.build();

		return ProductEntity.builder()
				.id(id)
				.name(name)
				.available(available)
				.seller(seller)
				.build();
	}

	private ProductPriceEntity createPrice(Long id, ProductEntity product) {

		ProductUnitEntity unit = new ProductUnitEntity();
		unit.setId(1L);
		unit.setName("кг");

		ProductPriceEntity price = new ProductPriceEntity();
		price.setId(id);
		price.setPrice(new BigDecimal("12.50"));
		price.setQty(2);
		price.setProduct(product);
		price.setUnit(unit);

		return price;
	}

	private ProductThumbnailProjection createThumbnail(Long productId, String thumbnail) {

		ProductThumbnailProjection projection = mock(ProductThumbnailProjection.class);

		when(projection.getProductId()).thenReturn(productId);
		when(projection.getThumbnail()).thenReturn(thumbnail);

		return projection;
	}

	private ProductVariantResponse createResponse(Long priceId, String productName, String thumbnail) {
		return createResponse(priceId, productName, thumbnail, true, 101L, "Seller 1");
	}

	private ProductVariantResponse createResponse(

			Long priceId,
			String productName,
			String thumbnail,
			boolean available) {
		return createResponse(priceId, productName, thumbnail, available, 101L, "Seller 1");
	}

	private ProductVariantResponse createResponse(
			Long priceId,
			String productName,
			String thumbnail,
			boolean available,
			Long sellerId,
			String sellerName) {

		return new ProductVariantResponse(
				new ProductVariantResponse.Product(
						1L, productName, available, new ProductVariantResponse.Images(thumbnail)),
				new ProductVariantResponse.Price(priceId, new BigDecimal("12.50"), 2),
				new ProductVariantResponse.Unit(1L, "кг"),
				new SellerResponse(sellerId, sellerName)
		);
	}

	@Nested
	@DisplayName("getVariants method")
	class GetVariantsMethod {

		@Test
		@DisplayName("should return variants in requested order with mapped product data")
		void shouldReturnProductVariantsWhenValidPriceIdsProvided() {

			ProductPriceEntity firstPrice = createPrice(10L, createProduct(1L, "First product", true));
			ProductPriceEntity secondPrice = createPrice(20L, createProduct(2L, "Second product", true));
			ProductThumbnailProjection firstThumbnail = createThumbnail(1L, "first-thumbnail");
			ProductThumbnailProjection secondThumbnail = createThumbnail(2L, "second-thumbnail");

			ProductVariantResponse secondResponse =
					createResponse(20L, "Second product", "second-thumbnail", true, 102L, "Seller 2");
			ProductVariantResponse firstResponse = createResponse(10L, "First product", "first-thumbnail");

			when(productPriceRepository.findByIdIn(List.of(20L, 10L)))
					.thenReturn(List.of(firstPrice, secondPrice));
			when(productImageRepository.findFirstThumbnailByProductIdIn(
					List.of(1L, 2L), ImageSize.THUMBNAIL))
					.thenReturn(List.of(firstThumbnail, secondThumbnail));

			when(productVariantMapper.toProductVariantResponse(secondPrice, "second-thumbnail"))
					.thenReturn(secondResponse);
			when(productVariantMapper.toProductVariantResponse(firstPrice, "first-thumbnail"))
					.thenReturn(firstResponse);

			List<ProductVariantResponse> actual =
					productVariantService.getVariants(List.of(20L, 10L, 20L));

			assertEquals(2, actual.size());
			assertEquals(20L, actual.get(0).price().id());
			assertEquals("Second product", actual.get(0).product().name());
			assertEquals("second-thumbnail", actual.get(0).product().images().thumbnail());
			assertEquals(10L, actual.get(1).price().id());
			assertEquals("First product", actual.get(1).product().name());


			assertEquals(new BigDecimal("12.50"), actual.get(0).price().value());
			assertEquals(2, actual.get(0).price().qty());
			assertEquals(1L, actual.get(0).unit().id());
			assertEquals("кг", actual.get(0).unit().name());
			assertEquals(102L, actual.get(0).seller().id());
			assertEquals("Seller 2", actual.get(0).seller().sellerName());
		}

		@Test
		@DisplayName("should omit missing prices from hydration response")
		void shouldOmitMissingPrices() {

			ProductPriceEntity existingPrice = createPrice(10L, createProduct(1L, "Product", true));
			ProductVariantResponse response = createResponse(10L, "Product", null);

			when(productPriceRepository.findByIdIn(List.of(10L, 999L)))
					.thenReturn(List.of(existingPrice));
			when(productImageRepository.findFirstThumbnailByProductIdIn(
					List.of(1L), ImageSize.THUMBNAIL)).thenReturn(List.of());
			when(productVariantMapper.toProductVariantResponse(existingPrice, null))
					.thenReturn(response);

			List<ProductVariantResponse> actual =
					productVariantService.getVariants(List.of(10L, 999L));

			assertEquals(1, actual.size());
			assertEquals(10L, actual.get(0).price().id());
			verify(productImageRepository).findFirstThumbnailByProductIdIn(
					List.of(1L), ImageSize.THUMBNAIL);
		}

		@Test
		@DisplayName("should return unavailable product without filtering it out")
		void shouldReturnUnavailableProduct() {

			ProductPriceEntity price = createPrice(10L, createProduct(1L, "Unavailable product", false));
			ProductVariantResponse response = createResponse(10L, "Unavailable product", null, false);

			when(productPriceRepository.findByIdIn(List.of(10L))).thenReturn(List.of(price));
			when(productImageRepository.findFirstThumbnailByProductIdIn(
					List.of(1L), ImageSize.THUMBNAIL)).thenReturn(List.of());
			when(productVariantMapper.toProductVariantResponse(price, null)).thenReturn(response);

			List<ProductVariantResponse> actual = productVariantService.getVariants(List.of(10L));

			assertFalse(actual.get(0).product().available());
			verify(productPriceRepository).findByIdIn(List.of(10L));
		}

		@Test
		@DisplayName("should load prices and images in batches without N+1 calls")
		void shouldLoadPricesAndImagesInBatches() {

			ProductPriceEntity firstPrice = createPrice(10L, createProduct(1L, "First", true));
			ProductPriceEntity secondPrice = createPrice(20L, createProduct(2L, "Second", true));
			ProductThumbnailProjection firstThumbnail = createThumbnail(1L, "first-thumbnail");
			ProductThumbnailProjection secondThumbnail = createThumbnail(2L, "second-thumbnail");

			when(productPriceRepository.findByIdIn(List.of(10L, 20L)))
					.thenReturn(List.of(firstPrice, secondPrice));
			when(productImageRepository.findFirstThumbnailByProductIdIn(
					List.of(1L, 2L), ImageSize.THUMBNAIL))
					.thenReturn(List.of(firstThumbnail, secondThumbnail));
			when(productVariantMapper.toProductVariantResponse(any(), any()))
					.thenReturn(createResponse(0L, "stub", null));

			productVariantService.getVariants(List.of(10L, 20L));

			verify(productPriceRepository, times(1)).findByIdIn(List.of(10L, 20L));
			verify(productImageRepository, times(1)).findFirstThumbnailByProductIdIn(
					List.of(1L, 2L), ImageSize.THUMBNAIL);
			verify(productPriceRepository, only()).findByIdIn(any());
			verify(productImageRepository, only()).findFirstThumbnailByProductIdIn(any(), any());
		}

		@Test
		@DisplayName("should return empty list when no prices exist for requested IDs")
		void shouldReturnEmptyListWhenNoPricesFound() {
			when(productPriceRepository.findByIdIn(List.of(999L))).thenReturn(List.of());

			List<ProductVariantResponse> actual = productVariantService.getVariants(List.of(999L));

			assertTrue(actual.isEmpty());
			verifyNoInteractions(productImageRepository);
			verifyNoInteractions(productVariantMapper);
		}
	}

	@Test
	@DisplayName("should reject empty price ids")
	void shouldRejectEmptyPriceIds() {

		InvalidPriceIdsException ex = assertThrows(
				InvalidPriceIdsException.class,
				() -> productVariantService.getVariants(List.of()));

		assertEquals(AT_LEAST_ONE_PRICE_ID_REQUIRED, ex.getMessage());

		verifyNoInteractions(productPriceRepository, productImageRepository);
	}

	@Test
	@DisplayName("should reject null price ids")
	void shouldRejectNullPriceIds() {

		InvalidPriceIdsException ex = assertThrows(
				InvalidPriceIdsException.class,
				() -> productVariantService.getVariants(null)
		);

		assertEquals(
				AT_LEAST_ONE_PRICE_ID_REQUIRED, ex.getMessage()
		);

		verifyNoInteractions(productPriceRepository, productImageRepository);
	}
}

package com.khutircraftubackend.product.price.service;

import com.khutircraftubackend.product.exception.InvalidPriceIdsException;
import com.khutircraftubackend.product.image.ImageSize;
import com.khutircraftubackend.product.image.ProductImageRepository;
import com.khutircraftubackend.product.image.ProductThumbnailProjection;
import com.khutircraftubackend.product.price.entity.ProductPriceEntity;
import com.khutircraftubackend.product.price.mapper.ProductVariantMapper;
import com.khutircraftubackend.product.price.repo.ProductPriceRepository;
import com.khutircraftubackend.product.price.response.ProductVariantResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.khutircraftubackend.product.exception.ProductResponseMessage.AT_LEAST_ONE_PRICE_ID_REQUIRED;

@Service
@RequiredArgsConstructor
public class ProductVariantService {

	private final ProductPriceRepository productPriceRepository;
	private final ProductImageRepository productImageRepository;
	private final ProductVariantMapper productVariantMapper;

	@Transactional(readOnly = true)
	public List<ProductVariantResponse> getVariants(Collection<Long> priceIds) {

		if (priceIds == null || priceIds.isEmpty()) {
			throw new InvalidPriceIdsException(AT_LEAST_ONE_PRICE_ID_REQUIRED);
		}

		List<Long> uniqueIds = priceIds.stream()
				.distinct()
				.toList();

		List<ProductPriceEntity> prices = productPriceRepository.findByIdIn(uniqueIds);

		Map<Long, ProductPriceEntity> pricesById = prices.stream()
				.collect(Collectors.toMap(ProductPriceEntity::getId,
						Function.identity()
				));

		Map<Long, String> thumbnailsByProductId = loadThumbnailsByProductId(prices);

		return uniqueIds.stream()
				.map(pricesById::get)
				.filter(Objects::nonNull)
				.map(price ->
						productVariantMapper.toProductVariantResponse(price,
								thumbnailsByProductId.get(price.getProduct().getId())))
				.toList();
	}

	private Map<Long, String> loadThumbnailsByProductId(

			List<ProductPriceEntity> prices) {

		if (prices.isEmpty()) {
			return Map.of();
		}

		List<Long> productIds = prices.stream()
				.map(price ->
						price.getProduct().getId())
				.distinct()
				.toList();

		return productImageRepository
				.findFirstThumbnailByProductIdIn(
						productIds,
						ImageSize.THUMBNAIL
				)
				.stream()
				.collect(Collectors.toMap(
						ProductThumbnailProjection::getProductId,
						ProductThumbnailProjection::getThumbnail,
						(first, second) -> first
				));
	}

}

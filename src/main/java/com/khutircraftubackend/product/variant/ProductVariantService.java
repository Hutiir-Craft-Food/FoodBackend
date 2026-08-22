package com.khutircraftubackend.product.variant;

import com.khutircraftubackend.product.ProductEntity;
import com.khutircraftubackend.product.image.ImageSize;
import com.khutircraftubackend.product.image.ProductImageRepository;
import com.khutircraftubackend.product.image.ProductThumbnailProjection;
import com.khutircraftubackend.product.price.entity.ProductPriceEntity;
import com.khutircraftubackend.product.price.repo.ProductPriceRepository;
import com.khutircraftubackend.product.variant.exception.InvalidPriceIdsException;
import com.khutircraftubackend.product.variant.response.ProductVariantDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductVariantService {

    public static final int MAX_PRICE_IDS = 100;

    private final ProductPriceRepository productPriceRepository;
    private final ProductImageRepository productImageRepository;

    @Transactional(readOnly = true)
    public List<ProductVariantDTO> getVariants(Collection<Long> priceIds) {
        validatePriceIds(priceIds);

        List<Long> requestedPriceIds = new ArrayList<>(priceIds);
        List<Long> uniquePriceIds = requestedPriceIds.stream().distinct().toList();
        List<ProductPriceEntity> prices = productPriceRepository.findByIdIn(uniquePriceIds);
        Map<Long, ProductPriceEntity> pricesById = prices.stream()
                .collect(Collectors.toMap(ProductPriceEntity::getId, Function.identity()));
        final Map<Long, String> thumbnailsByProductId = loadThumbnailsByProductId(prices);

        return uniquePriceIds.stream()
                .map(pricesById::get)
                .filter(Objects::nonNull)
                .map(price -> toProductVariant(
                        price, thumbnailsByProductId.get(price.getProduct().getId())))
                .toList();
    }

    private Map<Long, String> loadThumbnailsByProductId(List<ProductPriceEntity> prices) {
        if (prices.isEmpty()) {
            return Map.of();
        }

        List<Long> productIds = prices.stream()
                .map(price -> price.getProduct().getId())
                .distinct()
                .toList();

        return productImageRepository
                .findFirstThumbnailByProductIdIn(productIds, ImageSize.THUMBNAIL)
                .stream()
                .collect(Collectors.toMap(
                        ProductThumbnailProjection::getProductId,
                        ProductThumbnailProjection::getThumbnail,
                        (first, second) -> first
                ));
    }

    private void validatePriceIds(Collection<Long> priceIds) {
        if (priceIds == null || priceIds.isEmpty()
                || priceIds.size() > MAX_PRICE_IDS
                || priceIds.stream().anyMatch(id -> id == null || id <= 0)) {
            throw new InvalidPriceIdsException(
                    "priceIds must contain between one and " + MAX_PRICE_IDS
                            + " positive numeric IDs");
        }
    }

    private ProductVariantDTO toProductVariant(
            ProductPriceEntity priceEntity,
            String thumbnail) {
        ProductEntity product = priceEntity.getProduct();

        return new ProductVariantDTO(
                new ProductVariantDTO.Product(
                        product.getId(),
                        product.getName(),
                        product.isAvailable(),
                        new ProductVariantDTO.Images(thumbnail)
                ),
                new ProductVariantDTO.Price(
                        priceEntity.getId(),
                        priceEntity.getPrice(),
                        priceEntity.getQty()
                ),
                new ProductVariantDTO.Unit(
                        priceEntity.getUnit().getId(),
                        priceEntity.getUnit().getName()
                ),
                new ProductVariantDTO.Seller(
                        product.getSeller().getId(),
                        product.getSeller().getSellerName()
                )
        );
    }
}

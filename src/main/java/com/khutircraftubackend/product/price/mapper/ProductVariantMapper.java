package com.khutircraftubackend.product.price.mapper;

import com.khutircraftubackend.product.ProductEntity;
import com.khutircraftubackend.product.price.entity.ProductPriceEntity;
import com.khutircraftubackend.product.price.entity.ProductUnitEntity;
import com.khutircraftubackend.product.price.response.ProductVariantResponse;
import com.khutircraftubackend.seller.SellerMapper;
import org.mapstruct.*;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = SellerMapper.class)
public interface ProductVariantMapper {

	@Mapping(target = "product", expression = "java(toProduct(entity.getProduct(), thumbnail))")
	@Mapping(target = "price", source = "entity")
	@Mapping(target = "unit", source = "entity.unit")
	@Mapping(target = "seller", source = "entity.product.seller")
	ProductVariantResponse toProductVariantResponse(ProductPriceEntity entity, String thumbnail);

	@Mapping(target = "images", expression = "java(new ProductVariantResponse.Images(thumbnail))")
	ProductVariantResponse.Product toProduct(ProductEntity entity, String thumbnail);

	@Mapping(target = "value", source = "price")
	ProductVariantResponse.Price toPrice(ProductPriceEntity entity);

	ProductVariantResponse.Unit toUnit(ProductUnitEntity entity);

}

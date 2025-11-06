package com.khutircraftubackend.product;

import com.khutircraftubackend.category.CategoryMapper;
import com.khutircraftubackend.product.price.entity.ProductPriceEntity;
import com.khutircraftubackend.product.price.entity.ProductUnitEntity;
import com.khutircraftubackend.product.price.mapper.ProductPriceMapper;
import com.khutircraftubackend.product.request.ProductRequest;
import com.khutircraftubackend.product.response.ProductResponse;
import com.khutircraftubackend.seller.SellerMapper;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = SPRING, unmappedTargetPolicy = IGNORE,
        uses = {SellerMapper.class, CategoryMapper.class, ProductPriceMapper.class})
public interface ProductMapper {
	
	ProductEntity toProductEntity(ProductRequest request);
	
	@Mapping(target = "name", source = "request.name")
	@Mapping(target = "available", source = "request.available")
	@Mapping(target = "description", source = "request.description")
	@Mapping(target = "ingredients", source = "request.ingredients")
	@Mapping(target = "nutrition", source = "request.nutrition")
	@Mapping(target = "storage", source = "request.storage")
	@Mapping(target = "allergens", source = "request.allergens")
	void updateProductFromRequest(@MappingTarget ProductEntity product, ProductRequest request);

	ProductResponse toProductResponse(ProductEntity productEntity);
	
	@AfterMapping
	default void mapUnits(ProductEntity productEntity, @MappingTarget ProductResponse.ProductResponseBuilder builder) {
		
		if (productEntity.getPrices() == null) return;
		
		List<ProductUnitEntity> units = productEntity.getPrices().stream()
				.map(ProductPriceEntity::getUnit)
				.filter(Objects::nonNull)
				.distinct()
				.toList();
		
		builder.units(units);
	}
	
	Collection<ProductResponse> toProductResponse(Page<ProductEntity> productEntities);
	
}

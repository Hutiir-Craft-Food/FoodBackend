package com.khutircraftubackend.product;

import com.khutircraftubackend.category.CategoryMapper;
import com.khutircraftubackend.product.image.ProductImageMapper;
import com.khutircraftubackend.product.request.ProductRequest;
import com.khutircraftubackend.product.response.ProductResponse;
import com.khutircraftubackend.seller.SellerMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

import java.util.*;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = SPRING, unmappedTargetPolicy = IGNORE,
        uses = {SellerMapper.class, CategoryMapper.class, ProductImageMapper.class})
public interface ProductMapper {
	
	ProductEntity toProductEntity(ProductRequest request);
	
	@Mapping(target = "name", source = "request.name")
	@Mapping(target = "available", source = "request.available")
	@Mapping(target = "description", source = "request.description")
	void updateProductFromRequest(@MappingTarget ProductEntity product, ProductRequest request);

	@Mapping(target = "images", source = "images")
	ProductResponse toProductResponse(ProductEntity productEntity);

	Collection<ProductResponse> toProductResponse(Page<ProductEntity> productEntities);
}

package com.khutircraftubackend.cart;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collection;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = SPRING, unmappedTargetPolicy = IGNORE)
public interface CartItemMapper {

	@Mapping(target = "productPriceId", source = "productPrice.id")
	CartItemResponse toCartItemResponse(CartItemEntity cartItemEntity);

	Collection<CartItemResponse> toCartItemResponse(Collection<CartItemEntity> cartItemEntities);
}

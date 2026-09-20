package com.khutircraftubackend.cart;

import com.khutircraftubackend.product.ProductMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collection;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = SPRING, unmappedTargetPolicy = IGNORE,
        uses = {ProductMapper.class})
public interface CartItemMapper {

    @Mapping(target = "productPriceId", source = "cartItemEntity.productPrice.id")
    @Mapping(target = "quantity", source = "cartItemEntity.quantity")
    CartItemResponse toCartItemResponse(CartItemEntity cartItemEntity);

    Collection<CartItemResponse> toCartItemResponse(Collection<CartItemEntity> cartItemEntities);
}

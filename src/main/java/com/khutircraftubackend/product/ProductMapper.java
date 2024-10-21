package com.khutircraftubackend.product;

import com.khutircraftubackend.category.CategoryMapper;
import com.khutircraftubackend.product.request.ProductCreateRequest;
import com.khutircraftubackend.product.request.ProductUpdateRequest;
import com.khutircraftubackend.product.response.ProductResponse;
import com.khutircraftubackend.seller.SellerMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {SellerMapper.class, CategoryMapper.class})
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    ProductEntity toProductEntity(ProductCreateRequest request);
    
    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "available", source = "request.available")
    @Mapping(target = "description", source = "request.description")
    void updateProductFromRequest(@MappingTarget ProductEntity product, ProductUpdateRequest request);
    
    @Mapping(target = "seller", source = "productEntity.seller")
    @Mapping(target = "category", source = "productEntity.category")
    ProductResponse toProductResponse(ProductEntity productEntity);
    
    default Collection<ProductResponse> toProductResponse(Collection<ProductEntity> productEntities) {
        
        return productEntities.stream()
                .map(this::toProductResponse)
                .collect(Collectors.toList());
    }
    
    
}

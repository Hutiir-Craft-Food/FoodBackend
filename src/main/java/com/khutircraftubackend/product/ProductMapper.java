package com.khutircraftubackend.product;

import com.khutircraftubackend.product.image.FileConverterService;
import com.khutircraftubackend.product.request.ProductCreateRequest;
import com.khutircraftubackend.product.request.ProductUpdateRequest;
import com.khutircraftubackend.product.response.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {FileConverterService.class, ProductMapper.class})
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(target = "thumbnailImageUrl", source = "thumbnailImage", qualifiedByName = "multipartFileToString")
    @Mapping(target = "imageUrl", source = "image", qualifiedByName = "multipartFileToString")
    ProductEntity toProductEntity(ProductCreateRequest request);

    @Named("multipartFileToString")
    default String multipartFileToString(MultipartFile file) {
        
        return file != null ? file.getOriginalFilename() : null;
    }

    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "imageUrl", source = "request.image")
    @Mapping(target = "thumbnailImageUrl", source = "request.thumbnailImage")
    @Mapping(target = "available", source = "request.available")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "category.id", source = "request.categoryId")
    void updateProductFromRequest(@MappingTarget ProductEntity product, ProductUpdateRequest request);

    @Mapping(target = "name", source = "productEntity.name")
    @Mapping(target = "thumbnailImageUrl", source = "productEntity.thumbnailImageUrl")
    @Mapping(target = "imageUrl", source = "productEntity.imageUrl")
    @Mapping(target = "available", source = "productEntity.available")
    @Mapping(target = "description", source = "productEntity.description")
    @Mapping(target = "seller", source = "productEntity.seller")
    @Mapping(target = "category", source = "productEntity.category")
    ProductResponse toProductResponse(ProductEntity productEntity);
    
    default Collection<ProductResponse> toProductResponse(Collection<ProductEntity> productEntities) {
        
        return productEntities.stream()
                .map(this::toProductResponse)
                .collect(Collectors.toList());
    }
    
    
}

package com.khutircraftubackend.product;

import com.khutircraftubackend.category.CategoryMapper;
import com.khutircraftubackend.product.image.FileConverterService;
import com.khutircraftubackend.product.request.ProductCreateRequest;
import com.khutircraftubackend.product.request.ProductUpdateRequest;
import com.khutircraftubackend.product.response.ProductResponse;
import com.khutircraftubackend.seller.SellerEntity;
import com.khutircraftubackend.seller.SellerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {FileConverterService.class, CategoryMapper.class})
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
    @Mapping(target = "available", source = "request.available")
    @Mapping(target = "description", source = "request.description")
    void updateProductFromRequest(@MappingTarget ProductEntity product, ProductUpdateRequest request);
    
    @Mapping(target = "seller", source = "productEntity.seller", qualifiedByName = "toSellerResponse")
    @Mapping(target = "category", source = "productEntity.category")
    ProductResponse toProductResponse(ProductEntity productEntity);
    
    @Named("toSellerResponse")
    default SellerResponse toSellerResponse(SellerEntity seller) {
        
        if (seller == null) {
            
            return null;
        }
        
        return SellerResponse.builder()
                .id(seller.getId())
                .sellerName(seller.getSellerName())
                .companyName(seller.getCompanyName())
                .phoneNumber(seller.getPhoneNumber())
                .creationDate(seller.getCreationDate())
                .build();
    }
    
    default Collection<ProductResponse> toProductResponse(Collection<ProductEntity> productEntities) {
        
        return productEntities.stream()
                .map(this::toProductResponse)
                .collect(Collectors.toList());
    }
    
    
}

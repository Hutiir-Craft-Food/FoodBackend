package com.khutircraftubackend.product;

import com.khutircraftubackend.category.CategoryMapper;
import com.khutircraftubackend.product.image.FileConverterService;
import com.khutircraftubackend.product.request.ProductCreateRequest;
import com.khutircraftubackend.product.request.ProductUpdateRequest;
import com.khutircraftubackend.product.response.ProductResponse;
import com.khutircraftubackend.seller.SellerMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(unmappedTargetPolicy = IGNORE,
        componentModel = SPRING,
        uses = {SellerMapper.class, CategoryMapper.class}
)
public abstract class ProductMapper {

    @Autowired
    protected FileConverterService fileConverterService;

    @Mappings ({
        @Mapping(target = "thumbnailImageUrl", source = "thumbnailImage", qualifiedByName = "multipartFileToString"),
        @Mapping(target = "imageUrl", source = "image", qualifiedByName = "multipartFileToString"),
        @Mapping(target = "category.id", source = "categoryId"),
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "seller", ignore = true),
        @Mapping(target = "createdAt", ignore = true),
        @Mapping(target = "updatedAt", ignore = true)
    })
    public abstract ProductEntity toProductEntity(ProductCreateRequest request);

    @Mappings ({
        @Mapping(target = "thumbnailImageUrl", source = "request.thumbnailImage", qualifiedByName = "multipartFileToString"),
        @Mapping(target = "imageUrl", source = "request.image", qualifiedByName = "multipartFileToString"),
        @Mapping(target = "category.id", source = "request.categoryId"),
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "seller", ignore = true),
        @Mapping(target = "createdAt", ignore = true),
        @Mapping(target = "updatedAt", ignore = true)
    })
    public abstract void updateProductFromRequest(@MappingTarget ProductEntity product, ProductUpdateRequest request);

    public abstract ProductResponse toProductResponse(ProductEntity productEntity);

    public abstract Collection<ProductResponse> toProductResponse(Collection<ProductEntity> productEntities);

    @Named("multipartFileToString")
    protected String multipartFileToString(MultipartFile file) {
        return file != null ? file.getOriginalFilename() : null;
    }
}

package com.khutircraftubackend.product;

import com.khutircraftubackend.product.exception.product.ProductValidationMessages;
import com.khutircraftubackend.product.image.FileConverterService;
import com.khutircraftubackend.product.request.ProductCreateRequest;
import com.khutircraftubackend.product.request.ProductUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.web.multipart.MultipartFile;

@Mapper(componentModel = "spring", uses = {FileConverterService.class})
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(target = "thumbnailImageUrl", source = "thumbnailImage", qualifiedByName = "multipartFileToString")
    @Mapping(target = "imageUrl", source = "image", qualifiedByName = "multipartFileToString")
    ProductEntity toProductEntity(ProductCreateRequest request);

    @Named("multipartFileToString")
    default String multipartFileToString(MultipartFile file) {
        return null;
    }

    default void validateCreateRequest(ProductCreateRequest request) {
        if (request.name() == null || request.name().isEmpty()) {
            throw new IllegalArgumentException(ProductValidationMessages.NAME_NULL_OR_EMPTY);
        }
        if (request.description() == null) {
            throw new IllegalArgumentException(ProductValidationMessages.DESCRIPTION_NULL);
        }
        if (request.image() == null || request.image().isEmpty()) {
            throw new IllegalArgumentException(ProductValidationMessages.IMAGE_NULL_OR_EMPTY);
        }
        if (request.thumbnailImage() == null || request.thumbnailImage().isEmpty()) {
            throw new IllegalArgumentException(ProductValidationMessages.THUMBNAIL_IMAGE_NULL_OR_EMPTY);
        }
        if (request.available() == null) {
            throw new IllegalArgumentException(ProductValidationMessages.AVAILABLE_NULL);
        }
        if (request.seller() == null) {
            throw new IllegalArgumentException(ProductValidationMessages.SELLER_NULL);
        }
        if(request.categoryId() == null) {
            throw new IllegalArgumentException(ProductValidationMessages.CATEGORY_NULL_OR_EMPTY);
        }
    }

    default void validateUpdateRequest(ProductUpdateRequest request) {
        if (request.name() != null && request.name().isEmpty()) {
            throw new IllegalArgumentException(ProductValidationMessages.NAME_NULL_OR_EMPTY);
        }
        if (request.description() != null && request.description().isEmpty()) {
            throw new IllegalArgumentException(ProductValidationMessages.DESCRIPTION_NULL);
        }
        if (request.image() != null && request.image().isEmpty()) {
            throw new IllegalArgumentException(ProductValidationMessages.IMAGE_NULL_OR_EMPTY);
        }
        if (request.thumbnailImage() != null && request.thumbnailImage().isEmpty()) {
            throw new IllegalArgumentException(ProductValidationMessages.THUMBNAIL_IMAGE_NULL_OR_EMPTY);
        }
        if (request.available() != null) {
            throw new IllegalArgumentException(ProductValidationMessages.AVAILABLE_NULL);
        }
        if (request.categoryId() != null) {
            throw new IllegalArgumentException(ProductValidationMessages.CATEGORY_NULL_OR_EMPTY);
        }
    }

    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "imageUrl", source = "request.image")
    @Mapping(target = "thumbnailImageUrl", source = "request.thumbnailImage")
    @Mapping(target = "available", source = "request.available")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "category.id", source = "request.categoryId")
    void updateProductFromRequest(@MappingTarget ProductEntity product, ProductUpdateRequest request);

}

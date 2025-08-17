package com.khutircraftubackend.product.price.mapper;

import com.khutircraftubackend.product.ProductMapper;
import com.khutircraftubackend.product.price.entity.ProductUnitEntity;
import com.khutircraftubackend.product.price.request.ProductUnitRequest;
import com.khutircraftubackend.product.price.response.ProductUnitResponse;
import org.mapstruct.Mapper;

import java.util.Collection;
import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = SPRING, unmappedTargetPolicy = IGNORE,
        uses = {ProductPriceMapper.class, ProductMapper.class})
public interface ProductUnitMapper {
    
    ProductUnitEntity toUnitEntity(ProductUnitRequest request);
    
    ProductUnitResponse toUnitResponse(ProductUnitEntity entity);
    
    List<ProductUnitResponse> toUnitsResponses(Collection<ProductUnitEntity> entities);
}

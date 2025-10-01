package com.khutircraftubackend.product.price.mapper;

import com.khutircraftubackend.product.ProductMapper;
import com.khutircraftubackend.product.price.entity.ProductUnitEntity;
import com.khutircraftubackend.product.price.request.ProductUnitRequest;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = SPRING, unmappedTargetPolicy = IGNORE,
        uses = {ProductPriceMapper.class, ProductMapper.class})
public interface ProductUnitMapper {
    
    ProductUnitEntity toUnitEntity(ProductUnitRequest request);
}

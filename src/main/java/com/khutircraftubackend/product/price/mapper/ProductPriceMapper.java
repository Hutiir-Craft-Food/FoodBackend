package com.khutircraftubackend.product.price.mapper;

import com.khutircraftubackend.product.ProductMapper;
import com.khutircraftubackend.product.price.entity.ProductPriceEntity;
import com.khutircraftubackend.product.price.request.ProductPriceDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = SPRING, unmappedTargetPolicy = IGNORE,
        uses = {ProductUnitMapper.class, ProductMapper.class})
public interface ProductPriceMapper {
    
    @Mapping(target = "unit.id", source = "unitId")
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "price", source = "price", qualifiedByName = "normalizePrice")
    ProductPriceEntity toProductPriceEntity(ProductPriceDTO request);
    
    @Mapping(target = "unitId", source = "unit.id")
    ProductPriceDTO toProductPriceDTO(ProductPriceEntity entity);
    
    @Named("normalizePrice")
    default BigDecimal normalizePrice(BigDecimal price) {
        
        return price == null ? null : price.setScale(2, RoundingMode.HALF_UP);
    }
}

package com.khutircraftubackend.product.price.service;

import com.khutircraftubackend.product.exception.UnitNotBlank;
import com.khutircraftubackend.product.price.entity.ProductUnitEntity;
import com.khutircraftubackend.product.price.mapper.ProductUnitMapper;
import com.khutircraftubackend.product.price.repo.ProductUnitRepository;
import com.khutircraftubackend.product.price.request.ProductUnitRequest;
import com.khutircraftubackend.product.price.response.ProductUnitResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.khutircraftubackend.product.exception.ProductResponseMessage.UNIT_NOT_BLANK;

@Service
@RequiredArgsConstructor
public class ProductUnitService {
    
    private final ProductUnitMapper productUnitMapper;
    private final ProductUnitRepository productUnitRepository;
    
    @Transactional
    public ProductUnitResponse createUnit(ProductUnitRequest request) {
        
        if (request.name().isBlank()) {
            throw new UnitNotBlank(UNIT_NOT_BLANK);
        }
        ProductUnitEntity unit = productUnitMapper.toUnitEntity(request);
        ProductUnitEntity savedUnit = productUnitRepository.save(unit);
        
        return productUnitMapper.toUnitResponse(savedUnit);
    }
    
    
    public List<ProductUnitResponse> getProductUnits() {
        
        return productUnitMapper.toUnitsResponses(productUnitRepository.findAll());
    }
    
}

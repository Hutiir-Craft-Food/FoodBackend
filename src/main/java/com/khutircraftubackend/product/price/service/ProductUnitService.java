package com.khutircraftubackend.product.price.service;

import com.khutircraftubackend.product.exception.DuplicateUnitException;
import com.khutircraftubackend.product.exception.InvalidUnitException;
import com.khutircraftubackend.product.price.entity.ProductUnitEntity;
import com.khutircraftubackend.product.price.mapper.ProductUnitMapper;
import com.khutircraftubackend.product.price.repo.ProductUnitRepository;
import com.khutircraftubackend.product.price.request.ProductUnitRequest;
import com.khutircraftubackend.product.price.response.ProductUnitResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.khutircraftubackend.product.exception.ProductResponseMessage.UNIT_INVALID_NAME;

@Service
@RequiredArgsConstructor
public class ProductUnitService {
    
    private final ProductUnitMapper productUnitMapper;
    private final ProductUnitRepository productUnitRepository;
    
    @Transactional
    public ProductUnitResponse createUnit(ProductUnitRequest request) {
        
        ProductUnitEntity unit = productUnitMapper.toUnitEntity(request);
        String unitName = unit.getName();
    
        if (unitName == null || unitName.trim().isEmpty()) {
            throw new InvalidUnitException(UNIT_INVALID_NAME);
            
        }
        Optional<ProductUnitEntity> existingUnit = productUnitRepository.findByName(unitName);
        
        if (existingUnit.isPresent()) {
            throw new DuplicateUnitException(String.format(UNIT_INVALID_NAME, unitName));
        }
        
        ProductUnitEntity savedUnit = productUnitRepository.save(unit);
        
        return productUnitMapper.toUnitResponse(savedUnit);
    }
    
    
    public List<ProductUnitResponse> getProductUnits() {
        
        return productUnitMapper.toUnitsResponses(productUnitRepository.findAll());
    }
    
}

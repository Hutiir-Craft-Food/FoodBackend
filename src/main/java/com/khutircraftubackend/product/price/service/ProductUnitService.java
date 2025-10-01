package com.khutircraftubackend.product.price.service;

import com.khutircraftubackend.product.exception.DuplicateUnitException;
import com.khutircraftubackend.product.price.entity.ProductUnitEntity;
import com.khutircraftubackend.product.price.mapper.ProductUnitMapper;
import com.khutircraftubackend.product.price.repo.ProductUnitRepository;
import com.khutircraftubackend.product.price.request.ProductUnitRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.khutircraftubackend.product.exception.ProductResponseMessage.UNIT_INVALID_NAME;

@Service
@RequiredArgsConstructor
public class ProductUnitService {
    
    private final ProductUnitMapper productUnitMapper;
    private final ProductUnitRepository productUnitRepository;
    
    @Transactional
    public ProductUnitEntity createUnit(ProductUnitRequest request) {
        
        ProductUnitEntity unit = productUnitMapper.toUnitEntity(request);
        
        productUnitRepository.findByName(unit.getName())
                .ifPresent(existingUnit -> {
                    throw new DuplicateUnitException(
                            String.format(UNIT_INVALID_NAME, existingUnit.getName())
                    );
                });
    
        return productUnitRepository.save(unit);
    }
    
    
    public List<ProductUnitEntity> getProductUnits() {
        
        return productUnitRepository.findAll();
    }
    
}

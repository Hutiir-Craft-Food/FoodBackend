package com.khutircraftubackend.product.price.service;

import com.khutircraftubackend.product.ProductEntity;
import com.khutircraftubackend.product.ProductRepository;
import com.khutircraftubackend.product.exception.DuplicatePriceException;
import com.khutircraftubackend.product.exception.InvalidUnitException;
import com.khutircraftubackend.product.exception.PriceNotFoundException;
import com.khutircraftubackend.product.exception.ProductNotFoundException;
import com.khutircraftubackend.product.price.entity.ProductPriceEntity;
import com.khutircraftubackend.product.price.entity.ProductUnitEntity;
import com.khutircraftubackend.product.price.mapper.ProductPriceMapper;
import com.khutircraftubackend.product.price.repo.ProductPriceRepository;
import com.khutircraftubackend.product.price.repo.ProductUnitRepository;
import com.khutircraftubackend.product.price.request.ProductPriceRequest;
import com.khutircraftubackend.product.price.response.ProductPriceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.khutircraftubackend.product.exception.ProductResponseMessage.*;

@Service
@RequiredArgsConstructor
public class ProductPriceService {
    
    private final ProductUnitRepository productUnitRepository;
    private final ProductPriceRepository productPriceRepository;
    private final ProductRepository productRepository;
    private final ProductPriceMapper productPriceMapper;
    
    @Transactional
    public List<ProductPriceResponse> syncProductPrices(Long productId,
                                                        List<ProductPriceRequest> productPriceRequests) {
        
        ProductEntity productEntity = getProductEntityById(productId);
        
        Map<Long, ProductPriceEntity> existingPrices = productEntity.getPrices().stream()
                .collect(Collectors.toMap(ProductPriceEntity::getId, p -> p));
        
        Map<Long, ProductUnitEntity> units = loadUnits(productPriceRequests);
        
        List<ProductPriceEntity> updatedPrices = new ArrayList<>();
        
        for (ProductPriceRequest priceRequest : productPriceRequests) {
            validateDuplicate(updatedPrices, priceRequest, units);
            
            if (priceRequest.id() != null) {
                updateExistingPrice(existingPrices, priceRequest, updatedPrices, units);
            } else {
                createNewPrice(productEntity, priceRequest, updatedPrices, units);
            }
        }
        
        deleteRemovedPrices(existingPrices.values(), productEntity);
        productPriceRepository.saveAll(updatedPrices);
        
        return updatedPrices.stream()
                .map(productPriceMapper::toProductPriceResponse)
                .toList();
    }
    
    private ProductEntity getProductEntityById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND, productId));
    }
    
    private Map<Long, ProductUnitEntity> loadUnits(List<ProductPriceRequest> priceRequests) {
        Set<Long> unitIds = priceRequests.stream()
                .map(ProductPriceRequest::unitId)
                .collect(Collectors.toSet());
        
        return productUnitRepository.findAllById(unitIds).stream()
                .collect(Collectors.toMap(ProductUnitEntity::getId, u -> u));
    }
    
    private void validateDuplicate(List<ProductPriceEntity> updatedPrices,
                                   ProductPriceRequest priceRequest,
                                   Map<Long, ProductUnitEntity> units) {
        
        boolean duplicateExists = updatedPrices.stream()
                .anyMatch(p -> p.getUnit().getId().equals(priceRequest.unitId())
                        && p.getQty() == (priceRequest.qty()));
        
        if (duplicateExists) {
            throw new DuplicatePriceException(DUPLICATE, priceRequest.unitId(), priceRequest.qty());
        }
        
        if (!units.containsKey(priceRequest.unitId())) {
            throw new InvalidUnitException(INVALID_UNIT, priceRequest.unitId());
        }
    }
    
    private void updateExistingPrice(Map<Long, ProductPriceEntity> existingPrices,
                                     ProductPriceRequest priceRequest,
                                     List<ProductPriceEntity> updatedPrices,
                                     Map<Long, ProductUnitEntity> units) {
        ProductPriceEntity existing = existingPrices.remove(priceRequest.id());
        if (existing == null) {
            throw new PriceNotFoundException(PRICE_NOT_FOUND, priceRequest.id());
        }
        
        productPriceMapper.updateProductPriceFromRequest(existing, priceRequest);
        existing.setUnit(units.get(priceRequest.unitId()));
        
        updatedPrices.add(existing);
    }
    
    private void createNewPrice(ProductEntity product,
                                ProductPriceRequest priceRequest,
                                List<ProductPriceEntity> updatedPrices,
                                Map<Long, ProductUnitEntity> units) {
        ProductPriceEntity newPrice = productPriceMapper.toProductPriceEntity(priceRequest);
        newPrice.setProduct(product);
        newPrice.setUnit(units.get(priceRequest.unitId()));
        
        updatedPrices.add(newPrice);
    }
    
    private void deleteRemovedPrices(Collection<ProductPriceEntity> pricesToDelete, ProductEntity productEntity) {
        
        if (!pricesToDelete.isEmpty()) {
            productEntity.getPrices().removeAll(pricesToDelete);
        }
    }
    
    @Transactional(readOnly = true)
    public List<ProductPriceResponse> getProductPrices(Long productId) {
        
        ProductEntity productEntity = getProductEntityById(productId);
        
        return productEntity.getPrices().stream()
                .map(productPriceMapper::toProductPriceResponse)
                .toList();
    }
    
}

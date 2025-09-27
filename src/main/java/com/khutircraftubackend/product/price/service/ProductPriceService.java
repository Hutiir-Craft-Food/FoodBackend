package com.khutircraftubackend.product.price.service;

import com.khutircraftubackend.product.ProductEntity;
import com.khutircraftubackend.product.ProductRepository;
import com.khutircraftubackend.product.exception.ProductNotFoundException;
import com.khutircraftubackend.product.exception.UnitNotFoundException;
import com.khutircraftubackend.product.price.entity.ProductPriceEntity;
import com.khutircraftubackend.product.price.entity.ProductUnitEntity;
import com.khutircraftubackend.product.price.mapper.ProductPriceMapper;
import com.khutircraftubackend.product.price.mapper.ProductUnitMapper;
import com.khutircraftubackend.product.price.repo.ProductPriceRepository;
import com.khutircraftubackend.product.price.repo.ProductUnitRepository;
import com.khutircraftubackend.product.price.request.ProductPriceRequest;
import com.khutircraftubackend.product.price.response.ProductPriceResponse;
import com.khutircraftubackend.product.price.response.ProductUnitResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.khutircraftubackend.product.exception.ProductResponseMessage.PRODUCT_NOT_FOUND;
import static com.khutircraftubackend.product.exception.ProductResponseMessage.UNIT_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductPriceService {
    
    private final ProductUnitRepository productUnitRepository;
    private final ProductPriceRepository productPriceRepository;
    private final ProductRepository productRepository;
    private final ProductPriceMapper productPriceMapper;
    private final ProductUnitMapper productUnitMapper;
    
    @Transactional
    public List<ProductPriceResponse> syncProductPrices(Long productId,
                                                        List<ProductPriceRequest> productPriceRequests) {
        
        ProductEntity productEntity = getProductEntityById(productId);
        
        Map<Long, ProductUnitEntity> units = loadUnits(productPriceRequests);
        
        Map<String, ProductPriceEntity> existingPrices = existingPricesMap(productEntity);
        
        removePricesNotInRequest(productEntity, productPriceRequests);
        
        List<ProductPriceEntity> updatedPrices = buildUpdatePrices(
                productEntity, productPriceRequests, units, existingPrices);
        
        productPriceRepository.saveAll(updatedPrices);
        
        return updatedPrices.stream()
                .map(productPriceMapper::toProductPriceResponse)
                .toList();
    }
    
    
    private ProductEntity getProductEntityById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(
                        String.format(PRODUCT_NOT_FOUND, productId)));
    }
    
    
    private Map<Long, ProductUnitEntity> loadUnits(
            List<ProductPriceRequest> priceRequests) {
        
        Set<Long> unitIds = priceRequests.stream()
                .map(ProductPriceRequest::unitId)
                .collect(Collectors.toSet());
        
        Map<Long, ProductUnitEntity> units = productUnitRepository.findAllById(unitIds).stream()
                .collect(Collectors.toMap(ProductUnitEntity::getId, u -> u));
        
        if (units.size() != unitIds.size()) {
            Set<Long> missing = new HashSet<>(unitIds);
            missing.removeAll(units.keySet());
            log.warn("Missing units: {}", missing);
            throw new UnitNotFoundException(UNIT_NOT_FOUND);
        }
        return units;
    }
    
    
    private String buildKey(Long productId, Long unitId, int qty) {
        
        return productId + "-" + unitId + "-" + qty;
    }
    
    
    private Map<String, ProductPriceEntity> existingPricesMap(
            ProductEntity productEntity) {
        
        return productEntity.getPrices().stream()
                .collect(Collectors.toMap(
                        p -> buildKey(productEntity.getId(), p.getUnit().getId(), p.getQty()),
                        Function.identity()
                ));
    }
    
    private void removePricesNotInRequest(ProductEntity productEntity,
                                          List<ProductPriceRequest> productPriceRequests) {
        
        Set<String> requestKeys = productPriceRequests.stream()
                .map(req -> buildKey(productEntity.getId(), req.unitId(), req.qty()))
                .collect(Collectors.toSet());
    
    
        productEntity.getPrices().removeIf(p ->
                !requestKeys.contains(buildKey(productEntity.getId(), p.getUnit().getId(), p.getQty())));
    }
    
    
    private List<ProductPriceEntity> buildUpdatePrices(
            ProductEntity productEntity,
            List<ProductPriceRequest> priceRequests,
            Map<Long, ProductUnitEntity> units,
            Map<String, ProductPriceEntity> existingPrices) {
        
        List<ProductPriceEntity> updatePrices = new ArrayList<>();
        
        for (ProductPriceRequest req : priceRequests) {
            
            String key = buildKey(productEntity.getId(), req.unitId(), req.qty());
            
            ProductPriceEntity priceEntity = existingPrices.remove(key);
            
            if (priceEntity != null) {
                priceEntity.setPrice(req.price());
                updatePrices.add(priceEntity);
            } else {
                ProductPriceEntity newPrice = productPriceMapper.toProductPriceEntity(req);
                newPrice.setProduct(productEntity);
                newPrice.setUnit(units.get(req.unitId()));
                updatePrices.add(newPrice);
            }
        }
        return updatePrices;
    }
    
    
    @Transactional(readOnly = true)
    public List<ProductPriceResponse> getProductPrices(Long productId) {
        
        ProductEntity productEntity = getProductEntityById(productId);
        
        List<ProductUnitResponse> units = productUnitRepository.findAll().stream()
                .map(productUnitMapper::toUnitResponse)
                .toList();
        
        return productEntity.getPrices().stream()
                .map(price -> productPriceMapper.toProductPriceResponse(price, units))
                .toList();
    }
    
}

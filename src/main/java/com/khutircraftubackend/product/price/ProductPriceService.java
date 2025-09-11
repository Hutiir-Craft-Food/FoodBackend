package com.khutircraftubackend.product.price;

import com.khutircraftubackend.product.ProductEntity;
import com.khutircraftubackend.product.ProductRepository;
import com.khutircraftubackend.product.exception.*;
import com.khutircraftubackend.product.price.entity.ProductPriceEntity;
import com.khutircraftubackend.product.price.entity.ProductUnitEntity;
import com.khutircraftubackend.product.price.mapper.ProductPriceMapper;
import com.khutircraftubackend.product.price.repo.ProductPriceRepository;
import com.khutircraftubackend.product.price.repo.ProductUnitRepository;
import com.khutircraftubackend.product.price.request.ProductPriceRequest;
import com.khutircraftubackend.product.price.response.ProductPriceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.khutircraftubackend.product.exception.ProductResponseMessage.*;

@Service
@RequiredArgsConstructor
@Slf4j
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
                .orElseThrow(() -> new ProductNotFoundException(
                        String.format(PRODUCT_NOT_FOUND, productId)));
    }
    
    private Map<Long, ProductUnitEntity> loadUnits(List<ProductPriceRequest> priceRequests) {
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
    
    private void validateDuplicate(List<ProductPriceEntity> updatedPrices,
                                   ProductPriceRequest priceRequest,
                                   Map<Long, ProductUnitEntity> units) {
        
        boolean duplicateExists = updatedPrices.stream()
                .anyMatch(p -> p.getUnit().getId().equals(priceRequest.unitId())
                        && p.getQty() == (priceRequest.qty()));
        
        if (duplicateExists) {
            throw new DuplicatePriceException(
                    String.format(DUPLICATE, priceRequest.unitId(), priceRequest.qty()));
        }
        
        if (!units.containsKey(priceRequest.unitId())) {
            throw new InvalidUnitException(
                    String.format(INVALID_UNIT, priceRequest.unitId()));
        }
    }
    
    private void updateExistingPrice(Map<Long, ProductPriceEntity> existingPrices,
                                     ProductPriceRequest priceRequest,
                                     List<ProductPriceEntity> updatedPrices,
                                     Map<Long, ProductUnitEntity> units) {
        ProductPriceEntity existing = existingPrices.remove(priceRequest.id());
        
        if (existing == null) {
            throw new PriceNotFoundException(
                    String.format(PRICE_NOT_FOUND, priceRequest.id()));
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
        ProductUnitEntity unit = units.get(priceRequest.unitId());
        
        if(unit == null) {
            throw new InvalidUnitException(
                    String.format(INVALID_UNIT, priceRequest.unitId()));
        }
        newPrice.setUnit(unit);
        
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

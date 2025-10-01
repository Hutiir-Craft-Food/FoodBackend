package com.khutircraftubackend.product.price.service;

import com.khutircraftubackend.product.ProductEntity;
import com.khutircraftubackend.product.ProductRepository;
import com.khutircraftubackend.product.exception.ProductNotFoundException;
import com.khutircraftubackend.product.exception.UnitNotFoundException;
import com.khutircraftubackend.product.price.entity.ProductPriceEntity;
import com.khutircraftubackend.product.price.entity.ProductUnitEntity;
import com.khutircraftubackend.product.price.mapper.ProductPriceMapper;
import com.khutircraftubackend.product.price.repo.ProductPriceRepository;
import com.khutircraftubackend.product.price.repo.ProductUnitRepository;
import com.khutircraftubackend.product.price.request.ProductPriceDTO;
import com.khutircraftubackend.product.price.request.ProductPriceRequest;
import com.khutircraftubackend.product.price.response.ProductPriceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    
    @Transactional
    public ProductPriceResponse syncProductPrices(Long productId,
                                                  ProductPriceRequest request) {
        
        ProductEntity productEntity = getProductEntityById(productId);
        
        List<ProductPriceEntity> newPrices = request.prices().stream()
                .map(dto -> {
                    ProductPriceEntity entity = productPriceMapper.toProductPriceEntity(dto);
                    entity.setProduct(productEntity);
                    entity.setUnit(productUnitRepository.findById(dto.unitId())
                            .orElseThrow(() -> new UnitNotFoundException(
                                    (UNIT_NOT_FOUND))
                            ));
                    return entity;
                })
                .toList();
        
        productPriceRepository.deleteByProductId(productId);
        productPriceRepository.flush();
        productPriceRepository.saveAll(newPrices);
        
        List<ProductPriceDTO> prices = newPrices.stream()
                .map(productPriceMapper::toProductPriceDTO)
                .toList();
        
        return ProductPriceResponse.builder()
                .prices(prices)
                .build();
    }
    
    
    private ProductEntity getProductEntityById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(
                        String.format(PRODUCT_NOT_FOUND, productId)));
    }
    
    
    @Transactional(readOnly = true)
    public ProductPriceResponse getProductPrices(Long productId) {
        
        ProductEntity productEntity = getProductEntityById(productId);
        
        List<ProductUnitEntity> units = productUnitRepository.findAll();
        List<ProductPriceDTO> prices = productEntity.getPrices().stream()
                .map(productPriceMapper::toProductPriceDTO)
                .toList();
        
        return ProductPriceResponse.builder()
                .prices(prices)
                .units(units)
                .build();
    }
    
}

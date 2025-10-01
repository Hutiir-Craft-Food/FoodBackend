package com.khutircraftubackend.product.price;

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
import com.khutircraftubackend.product.price.request.ProductPriceDTO;
import com.khutircraftubackend.product.price.request.ProductPriceRequest;
import com.khutircraftubackend.product.price.response.ProductPriceResponse;
import com.khutircraftubackend.product.price.service.ProductPriceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductPriceServiceTest {
    
    @Mock
    private ProductUnitRepository productUnitRepository;
    
    @Mock
    private ProductPriceRepository productPriceRepository;
    
    @Mock
    private ProductRepository productRepository;
    
    @Spy
    @InjectMocks
    private ProductPriceMapper productPriceMapper = Mappers.getMapper(ProductPriceMapper.class);
    
    @Spy
    private ProductUnitMapper productUnitMapper = Mappers.getMapper(ProductUnitMapper.class);
    
    @InjectMocks
    private ProductPriceService productPriceService;
    
    private ProductEntity productEntity;
    
    @BeforeEach
    void setUp() {
        productEntity = new ProductEntity();
        productEntity.setId(1L);
        productEntity.setPrices(new ArrayList<>());
    }
    
    @Nested
    @DisplayName("syncProductPrices")
    class SyncProductPricesTests {
        
        @Test
        @DisplayName("should create new prices when valid request given")
        void shouldCreateNewPrices_WhenValidRequest() {
            
            // Arrange
            ProductPriceDTO dto = new ProductPriceDTO(new BigDecimal("1.00"), 10, 1L);
            ProductPriceRequest request = new ProductPriceRequest(List.of(dto));
            ProductUnitEntity unit = new ProductUnitEntity();
            
            unit.setId(1L);
            unit.setName("кг");
            
            when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));
            when(productUnitRepository.findById(1L)).thenReturn(Optional.of(unit));
            
            // Act
            ProductPriceResponse responses = productPriceService.syncProductPrices(1L, request);
            
            // Assert
            assertEquals(1, responses.prices().size());
            
            ProductPriceDTO response = responses.prices().get(0);
            
            assertEquals(new BigDecimal("1.00"), response.price());
            assertEquals(10, response.qty());
            
            assertEquals(1L, response.unitId());
            
            verify(productPriceRepository, times(1)).deleteByProductId(1L);
            verify(productPriceRepository, times(1)).saveAll(anyList());
            
        }
        
        
        @Test
        @DisplayName("should throw ProductNotFoundException when product does not exist")
        void shouldThrowProductNotFound_WhenProductMissing() {
            
            when(productRepository.findById(1L)).thenReturn(Optional.empty());
            
            ProductPriceRequest request = new ProductPriceRequest(Collections.emptyList());
            
            assertThrows(ProductNotFoundException.class,
                    () -> productPriceService.syncProductPrices(1L, request));
        }
        
        @Test
        @DisplayName("should throw UnitNotFoundException when unit missing")
        void shouldThrowUnitNotFound_WhenUnitMissing() {
            
            ProductPriceDTO dto = new ProductPriceDTO(new BigDecimal("1.00"), 10, 1L);
            ProductPriceRequest request = new ProductPriceRequest(List.of(dto));
            
            when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));
            when(productUnitRepository.findById(1L)).thenReturn(Optional.empty());
            
            assertThrows(UnitNotFoundException.class,
                    () -> productPriceService.syncProductPrices(1L, request));
        }
    }
    
    @Nested
    @DisplayName("getProductPrices")
    class GetProductPricesTests {
    
        @Test
        @DisplayName("should return mapped prices with correct units for existing product")
        void shouldReturnMappedPricesWithCorrectUnits_WhenProductExists() {
            
            ProductUnitEntity unit1 = new ProductUnitEntity();
            unit1.setId(1L);
            unit1.setName("кг");
            
            ProductUnitEntity unit2 = new ProductUnitEntity();
            unit2.setId(2L);
            unit2.setName("шт");
            
            ProductPriceEntity price1 = new ProductPriceEntity();
            price1.setId(1L);
            price1.setPrice(new BigDecimal("5.00"));
            price1.setQty(5);
            price1.setUnit(unit1);
            price1.setProduct(productEntity);
            
            ProductPriceEntity price2 = new ProductPriceEntity();
            price2.setId(2L);
            price2.setPrice(new BigDecimal("10.00"));
            price2.setQty(10);
            price2.setUnit(unit2);
            price2.setProduct(productEntity);
            
            productEntity.getPrices().addAll(List.of(price1, price2));
            
            when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));
            
            ProductPriceResponse responses = productPriceService.getProductPrices(1L);
            
            assertEquals(2, responses.prices().size());
            assertEquals(1L, responses.prices().get(0).unitId());
            assertEquals(2L, responses.prices().get(1).unitId());
            
            assertEquals(new BigDecimal("5.00"), responses.prices().get(0).price());
            assertEquals(5, responses.prices().get(0).qty());
            
            assertEquals(new BigDecimal("10.00"), responses.prices().get(1).price());
            assertEquals(10, responses.prices().get(1).qty());
        }
        
        @Test
        @DisplayName("should throw ProductNotFoundException when product missing")
        void shouldThrowProductNotFound_WhenProductMissing() {
            
            when(productRepository.findById(1L)).thenReturn(Optional.empty());
            
            assertThrows(ProductNotFoundException.class,
                    () -> productPriceService.getProductPrices(1L));
        }
    }
}
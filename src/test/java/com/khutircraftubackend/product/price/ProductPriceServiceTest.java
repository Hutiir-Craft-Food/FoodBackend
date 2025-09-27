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
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
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
        @DisplayName("should create new price when valid request given")
        void shouldCreateNewPrice_WhenValidRequest() {
            
            // Arrange
            List<ProductPriceRequest> priceRequests = List.of(
                    new ProductPriceRequest(null, new BigDecimal("1.00"), 10, 1L)
            );
            
            ProductUnitEntity unit = new ProductUnitEntity();
            unit.setId(1L);
            unit.setName("кг");
            
            when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));
            when(productUnitRepository.findAllById(Set.of(1L))).thenReturn(List.of(unit));
            
            // Act
            List<ProductPriceResponse> responses = productPriceService.syncProductPrices(1L, priceRequests);
            
            // Assert
            assertEquals(1, responses.size());
            
            ProductPriceResponse response = responses.get(0);
            
            assertNull(response.id());
            assertEquals(new BigDecimal("1.00"), response.price());
            assertEquals(10, response.qty());
            
            assertNotNull(response.unit());
            assertEquals(1L, response.unit().id());
            assertEquals("кг", response.unit().name());
            
            verify(productPriceRepository, times(1)).saveAll(anyList());
            
        }
        
        @Test
        @DisplayName("should update existing price when matching unit and qty exist")
        void shouldUpdateExistingPrice_WhenUnitAndQtyMatch() {
            
            // Arrange
            ProductUnitEntity unit = new ProductUnitEntity();
            unit.setId(1L);
            
            ProductPriceEntity existingPrice = new ProductPriceEntity();
            existingPrice.setId(1L);
            existingPrice.setPrice(new BigDecimal("1.00"));
            existingPrice.setQty(10);
            existingPrice.setUnit(unit);
            
            productEntity.getPrices().add(existingPrice);
            
            List<ProductPriceRequest> priceRequests = List.of(
                    new ProductPriceRequest(1L, new BigDecimal("2.00"), 20, 1L)
            );
            
            when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));
            when(productUnitRepository.findAllById(Set.of(1L))).thenReturn(List.of(unit));
            
            // Act
            List<ProductPriceResponse> responses = productPriceService.syncProductPrices(1L, priceRequests);
            
            // Assert
            assertEquals(1, responses.size());
            ProductPriceResponse response = responses.get(0);
            assertEquals(1L, response.id());
            assertEquals(new BigDecimal("2.00"), response.price());
            assertEquals(20, response.qty());
            assertEquals(1L, response.unit().id());
            
            verify(productPriceRepository, times(1)).saveAll(anyList());
        }
        
        @Test
        @DisplayName("should be equal when product and unit are the same")
        void shouldBeEqual_WhenProductAndUnitAreSame() {
            ProductUnitEntity unit = new ProductUnitEntity(1L, "кг");
            
            ProductPriceEntity p1 = new ProductPriceEntity();
            p1.setProduct(productEntity);
            p1.setUnit(unit);
            p1.setPrice(BigDecimal.valueOf(10.00));
            p1.setQty(5);
            
            ProductPriceEntity p2 = new ProductPriceEntity();
            p2.setProduct(productEntity);
            p2.setUnit(unit);
            p2.setPrice(BigDecimal.valueOf(20.00));
            p2.setQty(10);
            
            assertThat(p1).isEqualTo(p2);
            assertThat(p1.hashCode()).hasSameHashCodeAs(p2.hashCode());
        }
    
        
        @Test
        @DisplayName("should throw ProductNotFoundException when product does not exist")
        void shouldThrowProductNotFound_WhenProductMissing() {
            
            when(productRepository.findById(1L)).thenReturn(Optional.empty());
            
            assertThrows(ProductNotFoundException.class,
                    () -> productPriceService.syncProductPrices(1L, Collections.emptyList()));
        }
        
        @Test
        @DisplayName("should throw UnitNotFoundException when unit missing")
        void shouldThrowUnitNotFound_WhenUnitMissing() {
            
            List<ProductPriceRequest> priceRequests = List.of(
                    new ProductPriceRequest(null, new BigDecimal("1.00"), 10, 1L)
            );
            
            when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));
            when(productUnitRepository.findAllById(Set.of(1L))).thenReturn(Collections.emptyList());
            
            assertThrows(UnitNotFoundException.class,
                    () -> productPriceService.syncProductPrices(1L, priceRequests));
        }
    }
    
    @Nested
    @DisplayName("getProductPrices")
    class GetProductPricesTests {
        
        @Test
        @DisplayName("should return mapped prices when product exists")
        void shouldReturnMappedPrices_WhenProductExists() {
            
            ProductUnitEntity unit = new ProductUnitEntity();
            unit.setId(1L);
            unit.setName("кг");
            
            ProductPriceEntity price1 = new ProductPriceEntity();
            price1.setId(1L);
            price1.setPrice(new BigDecimal("5.00"));
            price1.setQty(5);
            price1.setUnit(unit);
            
            ProductPriceEntity price2 = new ProductPriceEntity();
            price2.setId(2L);
            price2.setPrice(new BigDecimal("10.00"));
            price2.setQty(10);
            price2.setUnit(unit);
            
            productEntity.getPrices().addAll(List.of(price1, price2));
            
            when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));
            
            List<ProductPriceResponse> responses = productPriceService.getProductPrices(1L);
            
            assertEquals(2, responses.size());
            assertEquals(1L, responses.get(0).id());
            assertEquals(2L, responses.get(1).id());
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
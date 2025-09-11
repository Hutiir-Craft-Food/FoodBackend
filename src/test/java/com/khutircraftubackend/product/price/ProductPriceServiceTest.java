package com.khutircraftubackend.product.price;

import com.khutircraftubackend.product.ProductEntity;
import com.khutircraftubackend.product.ProductRepository;
import com.khutircraftubackend.product.exception.*;
import com.khutircraftubackend.product.price.entity.ProductPriceEntity;
import com.khutircraftubackend.product.price.entity.ProductUnitEntity;
import com.khutircraftubackend.product.price.mapper.ProductPriceMapper;
import com.khutircraftubackend.product.price.mapper.ProductUnitMapper;
import com.khutircraftubackend.product.price.repo.ProductPriceRepository;
import com.khutircraftubackend.product.price.repo.ProductUnitRepository;
import com.khutircraftubackend.product.price.request.ProductPriceRequest;
import com.khutircraftubackend.product.price.response.ProductPriceResponse;
import com.khutircraftubackend.product.price.response.ProductUnitResponse;
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
        @DisplayName("should update existing price when id exists")
        void shouldUpdateExistingPrice_WhenIdExists() {
    
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
            assertSame(existingPrice, productEntity.getPrices().get(0));
            ProductPriceResponse response = responses.get(0);
            assertEquals(1L, response.id());
            assertEquals(new BigDecimal("2.00"), response.price());
            assertEquals(20, response.qty());
            assertEquals(1L, response.unit().id());
            
            verify(productPriceRepository, times(1)).saveAll(anyList());
        }
        
        @Test
        @DisplayName("should delete removed prices when not in request")
        void shouldDeleteRemovedPrices_WhenNotInRequest() {
            
            // Arrange
            ProductUnitEntity unit = new ProductUnitEntity();
            unit.setId(1L);
    
            ProductPriceEntity existingPrice1 = new ProductPriceEntity();
            existingPrice1.setId(1L);
            existingPrice1.setPrice(new BigDecimal("5.00"));
            existingPrice1.setQty(5);
            existingPrice1.setUnit(unit);
    
            ProductPriceEntity existingPrice2 = new ProductPriceEntity();
            existingPrice2.setId(2L);
            existingPrice2.setPrice(new BigDecimal("10.00"));
            existingPrice2.setQty(10);
            existingPrice2.setUnit(unit);
            
            productEntity.getPrices().addAll(List.of(existingPrice1, existingPrice2));
            
            List<ProductPriceRequest> priceRequests = List.of(
                    new ProductPriceRequest(1L, new BigDecimal("5.00"), 5, 1L)
            );
            
            when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));
            when(productUnitRepository.findAllById(Set.of(1L))).thenReturn(List.of(unit));
            
            ProductUnitResponse unitResponse = new ProductUnitResponse(1L, "кг");
            
            lenient().when(productPriceMapper.toProductPriceResponse(any()))
                    .thenAnswer(invocation -> {
                        ProductPriceEntity priceEntity = invocation.getArgument(0);
                        return new ProductPriceResponse(
                                priceEntity.getId(),
                                priceEntity.getPrice(),
                                priceEntity.getQty(),
                                unitResponse);
                    });
            
            // Act
            List<ProductPriceResponse> responses = productPriceService.syncProductPrices(1L, priceRequests);
            
            // Assert
            assertEquals(1, responses.size());
            assertEquals(1, productEntity.getPrices().size());
            assertSame(existingPrice1, productEntity.getPrices().get(0));
            
            verify(productPriceRepository, times(1)).saveAll(anyList());
        }
        
        @Test
        @DisplayName("should delete removed prices when request is empty")
        void shouldDeleteRemovedPrices_WhenRequestEmpty() {
            
            // Arrange
            ProductUnitEntity unit = new ProductUnitEntity();
            unit.setId(1L);
    
            ProductPriceEntity existingPrice1 = new ProductPriceEntity();
            existingPrice1.setId(1L);
            existingPrice1.setPrice(new BigDecimal("5.00"));
            existingPrice1.setQty(5);
            existingPrice1.setUnit(unit);
    
            ProductPriceEntity existingPrice2 = new ProductPriceEntity();
            existingPrice2.setId(2L);
            existingPrice2.setPrice(new BigDecimal("10.00"));
            existingPrice2.setQty(10);
            existingPrice2.setUnit(unit);
            
            productEntity.getPrices().addAll(List.of(existingPrice1, existingPrice2));
            
            List<ProductPriceRequest> priceRequests = List.of();
            
            when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));
            
            // Act
            List<ProductPriceResponse> responses = productPriceService.syncProductPrices(1L, priceRequests);
            
            // Assert
            assertEquals(0, responses.size());
            assertEquals(0, productEntity.getPrices().size());
            
            verify(productPriceRepository, times(1)).saveAll(Collections.emptyList());
            
        }
    
        @Test
        @DisplayName("should throw ProductNotFoundException when product does not exist")
        void shouldThrowProductNotFound_WhenProductDoesNotExist() {

            //Arrange
            lenient().when(productRepository.findProductById(1L)).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(ProductNotFoundException.class, () ->
                    productPriceService.syncProductPrices(1L, Collections.emptyList()));
        }
    
        @Test
        @DisplayName("should throw UnitNotFoundException when unitId missing")
        void shouldThrowUnitNotFound_WhenUnitIdMissing() {
            
                //Arrange
                List<ProductPriceRequest> priceRequests = List.of(
                        new ProductPriceRequest(null, new BigDecimal("1.00"), 10, 1L)
                );
    
                when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));
                when(productUnitRepository.findAllById(Set.of(1L))).thenReturn(List.of());
                
                //Act & Assert
                assertThrows(UnitNotFoundException.class, () ->
                        productPriceService.syncProductPrices(1L, priceRequests));
        }
    
        @Test
        @DisplayName("should throw DuplicatePriceException when same unit and qty duplicated")
        void shouldThrowDuplicatePriceException_WhenSameUnitAndQtyDuplicated() {
            
            //Arrange
            List<ProductPriceRequest> priceRequests = List.of(
                    new ProductPriceRequest(null, new BigDecimal("1.00"), 10, 1L),
                    new ProductPriceRequest(null, new BigDecimal("2.00"), 10, 1L)
            );
    
            ProductUnitEntity unit = new ProductUnitEntity();
            unit.setId(1L);
    
            when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));
            when(productUnitRepository.findAllById(Set.of(1L))).thenReturn(List.of(unit));
    
            //Act & Assert
            assertThrows(DuplicatePriceException.class, () ->
                    productPriceService.syncProductPrices(1L, priceRequests));
            
        }
    
        @Test
        @DisplayName("should throw PriceNotFoundException when updating non existent price")
        void shouldThrowPriceNotFound_WhenUpdatingNonExistentPrice() {
            
            //Arrange
            List<ProductPriceRequest> priceRequests = List.of(
                    new ProductPriceRequest(1L, new BigDecimal("1.00"), 10, 1L)
            );
    
            ProductUnitEntity unit = new ProductUnitEntity();
            unit.setId(1L);
    
            when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));
            when(productUnitRepository.findAllById(Set.of(1L))).thenReturn(List.of(unit));
    
            //Act & Assert
            assertThrows(PriceNotFoundException.class, () ->
                    productPriceService.syncProductPrices(1L, priceRequests));
        
        }
    
        @Test
        @DisplayName("should throw InvalidUnitException when unit not in map")
        void shouldThrowInvalidUnit_WhenUnitNotInMap() {
    
            //Arrange
            List<ProductPriceRequest> priceRequests = List.of(
                    new ProductPriceRequest(1L, new BigDecimal("1.00"), 10, 1L)
            );
    
            ProductUnitEntity unit = new ProductUnitEntity();
            unit.setId(null);
    
            when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));
            when(productUnitRepository.findAllById(Set.of(1L))).thenReturn(List.of(unit));
    
            //Act & Assert
            assertThrows(InvalidUnitException.class, () ->
                    productPriceService.syncProductPrices(1L, priceRequests));
            
        }
    
        @Test
        @DisplayName("should call saveAll with correct entities")
        void shouldCallSaveAll_WithCorrectEntities() {
            
            //Arrange
            List<ProductPriceRequest> priceRequests = List.of(
                    new ProductPriceRequest(null, new BigDecimal("1.00"), 10, 1L),
                    new ProductPriceRequest(null, new BigDecimal("2.00"), 20, 2L)
            );
    
            ProductUnitEntity unit1 = new ProductUnitEntity();
            unit1.setId(1L);
            unit1.setName("кг");
            
            ProductUnitEntity unit2 = new ProductUnitEntity();
            unit2.setId(2L);
            unit2.setName("л");
    
            when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));
            when(productUnitRepository.findAllById(Set.of(1L, 2L))).thenReturn(List.of(unit1, unit2));
            
            //Act
            List<ProductPriceResponse> responses = productPriceService.syncProductPrices(1L, priceRequests);
            
            //Assert
            assertEquals(2, responses.size());
            
            ProductPriceResponse response = responses.get(0);
            assertEquals(new BigDecimal("1.00"), response.price());
            assertEquals(10, response.qty());
            assertEquals(1L, response.unit().id());
            assertEquals("кг", response.unit().name());
            
            ProductPriceResponse response2 = responses.get(1);
            assertEquals(new BigDecimal("2.00"), response2.price());
            assertEquals(20, response2.qty());
            assertEquals(2L, response2.unit().id());
            assertEquals("л", response2.unit().name());
            
            verify(productPriceRepository, times(1)).saveAll(anyList());
    
        }
    }
    
    @Nested
    @DisplayName("getProductPrices")
    class GetProductPricesTests {
        
        @Test
        @DisplayName("should return mapped prices when product exists")
        void shouldReturnMappedPrices_WhenProductExists() {
            
            // Arrange
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
            
            // Act
            List<ProductPriceResponse> responses = productPriceService.getProductPrices(1L);
            
            // Assert
            assertEquals(2, responses.size());
            
            ProductPriceResponse response1 = responses.get(0);
            assertEquals(1L, response1.id());
            assertEquals(new BigDecimal("5.00"), response1.price());
            assertEquals(5, response1.qty());
            assertEquals(1L, response1.unit().id());
            assertEquals("кг", response1.unit().name());
            
            ProductPriceResponse response2 = responses.get(1);
            assertEquals(2L, response2.id());
            assertEquals(new BigDecimal("10.00"), response2.price());
            assertEquals(10, response2.qty());
            assertEquals(1L, response2.unit().id());
            assertEquals("кг", response2.unit().name());
        }
    
        @Test
        @DisplayName("should throw ProductNotFoundException when product missing")
        void shouldThrowProductNotFound_WhenProductMissing() {
            
            //Arrange
            when(productRepository.findById(1L)).thenReturn(Optional.empty());
    
            //Act & Assert
            assertThrows(ProductNotFoundException.class, () ->
                    productPriceService.getProductPrices(1L));
        }
    }
}
package com.khutircraftubackend.product.price.controller;

import com.khutircraftubackend.product.price.request.ProductPriceRequest;
import com.khutircraftubackend.product.price.response.ProductPriceResponse;
import com.khutircraftubackend.product.price.ProductPriceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/products/{productId}/prices")
@RequiredArgsConstructor
public class ProductPriceController {
    
    private final ProductPriceService productPriceService;
    
    @GetMapping
    public List<ProductPriceResponse> getPrices(
            @PathVariable Long productId) {
        
        return productPriceService.getProductPrices(productId);
        
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or (hasRole('SELLER') and @productService.canModifyProduct(#productId))")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductPriceResponse> syncPrices(
            @PathVariable Long productId,
            @RequestBody @Valid List<ProductPriceRequest> prices) {
        
        return productPriceService.syncProductPrices(productId, prices);
    }
    
}

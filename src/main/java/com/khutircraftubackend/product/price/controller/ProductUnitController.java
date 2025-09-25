package com.khutircraftubackend.product.price.controller;

import com.khutircraftubackend.product.price.ProductUnitService;
import com.khutircraftubackend.product.price.request.ProductUnitRequest;
import com.khutircraftubackend.product.price.response.ProductUnitResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/products/units")
@RequiredArgsConstructor
public class ProductUnitController {
    
    private final ProductUnitService productUnitService;
    
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<ProductUnitResponse> getAllUnits() {
        
        return productUnitService.getProductUnits();
    }
    
    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductUnitResponse createUnit(@Valid @RequestBody ProductUnitRequest request) {
        
        return productUnitService.createUnit(request);
    }
    
}

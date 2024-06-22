package com.gmail.ypon2003.marketplacebackend.factory;

import com.gmail.ypon2003.marketplacebackend.dto.ProductDTO;
import com.gmail.ypon2003.marketplacebackend.models.Product;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/*
Клас ProductFactory служить для перетворення моделі Product
у DTO ProductDTO.
 */
@Component
public class ProductFactory implements Function<Product, ProductDTO> {

    @Override
    public ProductDTO apply(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .infoSeller(product.getInfoSeller())
                .measurement(product.getMeasurement())
                .build();
    }
}

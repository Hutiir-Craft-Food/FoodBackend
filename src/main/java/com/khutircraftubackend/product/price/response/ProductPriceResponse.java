package com.khutircraftubackend.product.price.response;

import com.fasterxml.jackson.annotation.JsonView;
import com.khutircraftubackend.product.price.view.Views;

import java.math.BigDecimal;
import java.util.List;

public record ProductPriceResponse(
        
        @JsonView({Views.Get.class, Views.Post.class})
        Long id,
        
        @JsonView({Views.Get.class, Views.Post.class})
        BigDecimal price,
        
        @JsonView({Views.Get.class, Views.Post.class})
        int qty,
        
        @JsonView({Views.Get.class})
        List<ProductUnitResponse> units,
        
        @JsonView({Views.Post.class})
        ProductUnitResponse unit
) {
}

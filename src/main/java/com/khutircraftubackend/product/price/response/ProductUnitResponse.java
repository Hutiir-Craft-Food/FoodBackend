package com.khutircraftubackend.product.price.response;

import com.fasterxml.jackson.annotation.JsonView;
import com.khutircraftubackend.product.price.view.Views;

public record ProductUnitResponse(
        
        @JsonView({Views.Get.class})
        Long id,
        
        @JsonView({Views.Get.class, Views.Post.class})
        String name
) {
}

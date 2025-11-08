package com.khutircraftubackend.product.image.response;

import com.fasterxml.jackson.annotation.JsonView;
import com.khutircraftubackend.common.view.Public;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductImageDTO {
    private Long id;
    private Long productId;
    private String uid;

    @JsonView(Public.class)
    private int position;

    @JsonView(Public.class)
    private ImageLinks links;
}
package com.khutircraftubackend.product.image.response;

import com.fasterxml.jackson.annotation.JsonView;
import com.khutircraftubackend.common.view.Public;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonView(Public.class)
public class ImageLinks {
    private String thumbnail;
    private String small;
    private String medium;
    private String large;
}

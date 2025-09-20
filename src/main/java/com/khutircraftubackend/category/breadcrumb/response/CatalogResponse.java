package com.khutircraftubackend.category.breadcrumb.response;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatalogResponse {
    
    private Long id;
    private String name;
    
    @Builder.Default
    private List<CatalogResponse> children = new ArrayList<>();
}

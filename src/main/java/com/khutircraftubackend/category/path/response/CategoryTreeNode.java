package com.khutircraftubackend.category.path.response;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryTreeNode {
    
    private Long id;
    private String name;
    
    @Builder.Default
    private List<CategoryTreeNode> children = new ArrayList<>();
}

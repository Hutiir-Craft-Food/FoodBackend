package com.khutircraftubackend.category.catalog.response;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryTreeNode {
    
    private Long id;
    private String name;
    private List<CategoryTreeNode> children = new ArrayList<>();
}

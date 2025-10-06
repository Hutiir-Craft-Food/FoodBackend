package com.khutircraftubackend.category.catalog;

import com.khutircraftubackend.category.catalog.response.CategoryTreeNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/categories")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService catalogService;

    @GetMapping("/catalog")
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryTreeNode> getCategoryCatalog() {

        return catalogService.getCatalogTree();
    }

    @GetMapping("/catalog/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryTreeNode getCategoryCatalog(@PathVariable Long categoryId) {

        return catalogService.getCatalogTree(categoryId);
    }
}

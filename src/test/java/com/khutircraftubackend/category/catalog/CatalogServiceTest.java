package com.khutircraftubackend.category.catalog;

import com.khutircraftubackend.category.CategoryEntity;
import com.khutircraftubackend.category.CategoryRepository;
import com.khutircraftubackend.category.catalog.response.CategoryTreeNode;
import com.khutircraftubackend.category.exception.CategoryNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

    @Mock
    private CategoryRepository categoryRepository;
    
    @InjectMocks
    private CatalogService catalogService;
    
    @BeforeEach
    void setUp() {
    }
    
    @Test
    @DisplayName("getCatalogTree_shouldReturnRootWithChildren")
    void getCatalogTree_shouldReturnRootWithChildren() {
        
        // Arrange
        CategoryEntity root = new CategoryEntity();
        root.setId(1L);
        root.setName("Root");
        
        CategoryEntity child = new CategoryEntity();
        child.setId(2L);
        child.setName("Child");
        child.setParentCategory(root);
        
        // Act
        when(categoryRepository.findAll()).thenReturn(List.of(root, child));
        
        // Assert
        List<CategoryTreeNode> tree = catalogService.getCatalogTree();
        
        assertThat(tree).hasSize(1);
        assertThat(tree.get(0).getName()).isEqualTo("Root");
        assertThat(tree.get(0).getChildren()).hasSize(1);
        assertThat(tree.get(0).getChildren().get(0).getName()).isEqualTo("Child");
    }
    
    @Test
    @DisplayName("getCatalogTreeById_shouldBuildParentPath")
    void getCatalogTreeById_shouldBuildParentPath() {
        
        // Arrange
        CategoryEntity root = new CategoryEntity();
        root.setId(1L);
        root.setName("Root");
        
        CategoryEntity child = new CategoryEntity();
        child.setId(2L);
        child.setName("Child");
        child.setParentCategory(root);
        
        // Act
        when(categoryRepository.findAll()).thenReturn(List.of(root, child));
        
        // Assert
        CategoryTreeNode result = catalogService.getCategoryPathToRoot(2L);
        
        assertThat(result.getName()).isEqualTo("Root");
        assertThat(result.getChildren()).hasSize(1);
        assertThat(result.getChildren().get(0).getName()).isEqualTo("Child");
    }
    
    @Test
    @DisplayName("getCatalogTreeById_shouldThrowExceptionIfCategoryNotFound")
    void getCatalogTreeById_shouldThrowExceptionIfCategoryNotFound() {
        
        // Arrange
        when(categoryRepository.findAll()).thenReturn(List.of());
        
        // Act & Assert
        assertThatThrownBy(() -> catalogService.getCategoryPathToRoot(99L))
                .isInstanceOf(CategoryNotFoundException.class);
    }
    
    @Test
    @DisplayName("getCatalogTree_shouldReturnMultipleRoots")
    void getCatalogTree_shouldReturnMultipleRoots() {
        
        // Arrange
        CategoryEntity root1 = new CategoryEntity();
        root1.setId(1L);
        root1.setName("Root1");
        
        CategoryEntity root2 = new CategoryEntity();
        root2.setId(2L);
        root2.setName("Root2");
        
        // Act
        when(categoryRepository.findAll()).thenReturn(List.of(root1, root2));
    
        // Assert
        List<CategoryTreeNode> tree = catalogService.getCatalogTree();
        
        assertThat(tree).hasSize(2);
        assertThat(tree).extracting(CategoryTreeNode::getName)
                .containsExactlyInAnyOrder("Root1", "Root2");
    }
    
    @Test
    @DisplayName("getCatalogTree_shouldReturnEmptyListWhenNoCategories")
    void getCatalogTree_shouldReturnEmptyListWhenNoCategories() {
        
        //Arrange
        when(categoryRepository.findAll()).thenReturn(List.of());
        
        //Act
        List<CategoryTreeNode> tree = catalogService.getCatalogTree();
        
        //Assert
        assertThat(tree).isEmpty();
    }
    
    @Test
    @DisplayName("getCatalogTree_shouldBuildNestedHierarchy")
    void getCatalogTree_shouldBuildNestedHierarchy() {
        
        // Arrange
        CategoryEntity root = new CategoryEntity();
        root.setId(1L);
        root.setName("Root");
        
        CategoryEntity child = new CategoryEntity();
        child.setId(2L);
        child.setName("Child");
        child.setParentCategory(root);
        
        CategoryEntity grandchild = new CategoryEntity();
        grandchild.setId(3L);
        grandchild.setName("Grandchild");
        grandchild.setParentCategory(child);
        
        when(categoryRepository.findAll()).thenReturn(List.of(root, child, grandchild));
        
        // Act
        List<CategoryTreeNode> tree = catalogService.getCatalogTree();
        
        // Assert
        assertThat(tree).hasSize(1);
        assertThat(tree.get(0).getChildren()).hasSize(1);
        assertThat(tree.get(0).getChildren().get(0).getChildren()).hasSize(1);
        assertThat(tree.get(0).getChildren().get(0).getChildren().get(0).getName())
                .isEqualTo("Grandchild");
    }
}
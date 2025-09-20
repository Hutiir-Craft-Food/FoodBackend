package com.khutircraftubackend.category.breadcrumb;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryViewRepository extends JpaRepository<CategoryViewEntity, Long> {
    
    @NonNull
    List<CategoryViewEntity> findAll();
    
    @NonNull
    Optional<CategoryViewEntity> findById(@NonNull Long id);
    
}

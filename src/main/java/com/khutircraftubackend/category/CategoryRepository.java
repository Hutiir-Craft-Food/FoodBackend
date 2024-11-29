package com.khutircraftubackend.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
	List<CategoryEntity> findAllByParentCategoryIsNull();
	
	List<CategoryEntity> findAllByParentCategory_Id(Long id);
}

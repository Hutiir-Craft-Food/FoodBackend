package com.khutircraftubackend.product.price.repo;

import com.khutircraftubackend.product.price.entity.ProductPriceEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ProductPriceRepository extends JpaRepository<ProductPriceEntity, Long> {

	void deleteByProductId(Long productId);

	@EntityGraph(attributePaths = {
			"product",
			"product.seller",
			"unit"
	})
	List<ProductPriceEntity> findByIdIn(Collection<Long> ids);

	@Query("SELECT price.id FROM ProductPriceEntity price WHERE price.id IN :ids")
	List<Long> findExistingIdsByIdIn(@Param("ids") Collection<Long> ids);

}

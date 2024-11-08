package com.khutircraftubackend.delivery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryMethodRepository extends JpaRepository<DeliveryMethodEntity, Long> {
	
	List<DeliveryMethodEntity> findBySellerId(Long id);

}

package com.khutircraftubackend.delivery;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.Collection;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = SPRING, unmappedTargetPolicy = IGNORE)
public interface DeliveryMethodMapper {
	
	DeliveryMethodEntity toDeliveryMethodEntity(DeliveryMethodRequest request);
	
	void updateDeliveryMethodFromRequest(@MappingTarget DeliveryMethodEntity entity, DeliveryMethodRequest request);
	
	DeliveryMethodResponse toDeliveryMethodResponse(DeliveryMethodEntity entity);
	
	Collection<DeliveryMethodResponse> toDeliveryMethodResponse(Collection<DeliveryMethodEntity> deliveryMethodEntities);
}

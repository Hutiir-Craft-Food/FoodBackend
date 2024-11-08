package com.khutircraftubackend.address;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = SPRING, unmappedTargetPolicy = IGNORE,
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AddressMapper {
	AddressEntity toAddressEntity(AddressRequest addressRequest);
	@Mapping(target = "country", source = "addressRequest.country")
	@Mapping(target = "city", source = "addressRequest.city")
	@Mapping(target = "street", source = "addressRequest.street")
	@Mapping(target = "houseNumber", source = "addressRequest.houseNumber")
	@Mapping(target = "apartmentNumber", source = "addressRequest.apartmentNumber")
	@Mapping(target = "postalCode", source = "addressRequest.postalCode")
	void updateAddressFromRequest(@MappingTarget AddressEntity addressEntity, AddressRequest addressRequest);
	AddressResponse toAddressResponse(AddressEntity addressEntity);
	
}

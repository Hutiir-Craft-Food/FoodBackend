package com.khutircraftubackend.seller;

import com.khutircraftubackend.address.AddressMapper;
import com.khutircraftubackend.auth.request.RegisterRequest;
import com.khutircraftubackend.delivery.DeliveryMethodMapper;
import com.khutircraftubackend.seller.qualityCertificates.QualityCertificateMapper;
import com.khutircraftubackend.seller.request.SellerRequest;
import com.khutircraftubackend.seller.response.SellerResponse;
import com.khutircraftubackend.user.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.IGNORE;

/**
 * Інтерфейс SellerMapper мапить дані між моделлю SellerEntity та DTO SellerRequest.
 */

@Mapper(componentModel = SPRING, unmappedTargetPolicy = IGNORE,
		nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
		uses = {QualityCertificateMapper.class, DeliveryMethodMapper.class, AddressMapper.class})
public interface SellerMapper {
	@Mapping(target = "user", source = "user")
	@Mapping(target = "sellerName", source = "request.details.sellerName")
	@Mapping(target = "companyName", source = "request.details.companyName")
	@Mapping(target = "phoneNumber", source = "request.details.phoneNumber")
	@Mapping(target = "customerPhoneNumber", source = "request.details.customerPhoneNumber")
	@Mapping(target = "description", source = "request.details.description")
	SellerEntity toSellerEntity(RegisterRequest request, UserEntity user);
	
	@Mapping(target = "companyName", source = "request.companyName")
	@Mapping(target = "sellerName", source = "request.sellerName")
	@Mapping(target = "phoneNumber", source = "request.phoneNumber")
	@Mapping(target = "customerPhoneNumber", source = "request.customerPhoneNumber")
	@Mapping(target = "description", source = "request.description")
	@Mapping(target = "address", ignore = true)
	void updateSellerFromRequest(@MappingTarget SellerEntity seller, SellerRequest request);
	
	//TODO Deleted all methods delivery or isActive method
	@Mapping(target = "phoneNumber", ignore = true)
	@Mapping(target = "addressResponse", source = "address")
	@Mapping(target = "deliveryMethodResponse", source = "deliveryMethods")
	@Mapping(target = "activeDeliveryMethods",
			expression = "java(deliveryMethodMapper.toDeliveryMethodResponse(" +
					"sellerEntity.getDeliveryMethods().stream()" +
					".filter(DeliveryMethodEntity::getIsActive).toList()))")
	SellerResponse toSellerResponse(SellerEntity sellerEntity);
	
}

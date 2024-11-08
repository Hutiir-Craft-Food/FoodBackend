package com.khutircraftubackend.seller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.khutircraftubackend.address.AddressResponse;
import com.khutircraftubackend.delivery.DeliveryMethodResponse;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Collection;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record SellerResponse(
		Long id,
		String sellerName,
		String companyName,
		String description,
		String phoneNumber,
		String customerPhoneNumber,
		String logoUrl,
		LocalDateTime creationDate,
		AddressResponse addressResponse,
		
		//TODO which method is required?
		Collection<DeliveryMethodResponse> deliveryMethodResponse,
		Collection<DeliveryMethodResponse>activeDeliveryMethods
) {
}

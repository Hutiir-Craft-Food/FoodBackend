package com.khutircraftubackend.address;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record AddressResponse(
		Long id,
		String country,
		String city,
		String street,
		String houseNumber,
        String apartmentNumber,
		String postalCode
) {
}

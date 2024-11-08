package com.khutircraftubackend.address;

import lombok.Builder;

@Builder
public record AddressRequest(
		Long id,
		String country,
		String city,
		String street,
		String houseNumber,
        String apartmentNumber,
		String postalCode
) {

}

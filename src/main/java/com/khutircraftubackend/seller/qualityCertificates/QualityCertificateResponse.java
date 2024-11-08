package com.khutircraftubackend.seller.qualityCertificates;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record QualityCertificateResponse(
		Long id,
		String name,
		String description,
		String certificateUrl,
        String issue_date,
        String expiration_date,
        String sellerCompanyName
) {
}

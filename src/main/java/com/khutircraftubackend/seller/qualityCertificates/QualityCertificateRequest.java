package com.khutircraftubackend.seller.qualityCertificates;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.khutircraftubackend.seller.SellerEntity;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record QualityCertificateRequest(
		String name,
		@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
		LocalDateTime issue_date,
		@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
		LocalDateTime expiration_date,
        String description,
		SellerEntity seller
) {
}

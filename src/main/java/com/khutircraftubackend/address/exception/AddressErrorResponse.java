package com.khutircraftubackend.address.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.util.Date;

@Builder
public record AddressErrorResponse(
		
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
		Date timestamp,
		int status,
		String error,
		String message,
		String path
) {
	public AddressErrorResponse {
		timestamp = new Date();
	}
}

package com.khutircraftubackend.auth.request;

import lombok.Builder;

@Builder
public record ConfirmationRequest(
        String email,
        String jwt
) {
}

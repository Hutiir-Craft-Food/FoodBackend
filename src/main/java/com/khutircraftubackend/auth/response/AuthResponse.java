package com.khutircraftubackend.auth.response;

import lombok.Builder;

@Builder
public record AuthResponse(
        String jwt,
        String email
) {
}

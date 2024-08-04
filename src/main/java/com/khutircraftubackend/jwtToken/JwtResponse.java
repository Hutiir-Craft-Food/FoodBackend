package com.khutircraftubackend.jwtToken;

import lombok.Builder;

@Builder
public record JwtResponse(
        String jwt
) {
}

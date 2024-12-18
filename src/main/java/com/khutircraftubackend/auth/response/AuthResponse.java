package com.khutircraftubackend.auth.response;

import com.khutircraftubackend.user.Role;
import lombok.Builder;

@Builder
public record AuthResponse(
        String jwt,
        Role role,
        boolean confirmed
) {
}

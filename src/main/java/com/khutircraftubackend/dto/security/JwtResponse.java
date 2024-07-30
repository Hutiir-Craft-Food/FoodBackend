package com.khutircraftubackend.dto.security;

import com.khutircraftubackend.models.Role;
import lombok.Getter;
import lombok.Setter;

/**
 * Клас JwtResponse використовується для передачі JWT токену у відповідь на успішну авторизацію.
 */

@Getter
@Setter
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String email;
    private Role role;

    public JwtResponse(String jwt) {
    }
}

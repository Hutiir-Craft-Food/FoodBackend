package com.khutircraftubackend.auth;

import com.khutircraftubackend.auth.request.LoginRequest;
import com.khutircraftubackend.auth.request.RegisterRequest;
import com.khutircraftubackend.auth.response.AuthResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationControllerImpl implements AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthResponse authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return authenticationService.authenticate(loginRequest);
    }

    public AuthResponse register(@Valid @RequestBody RegisterRequest registerRequest) {
        return authenticationService.registerNewUser(registerRequest);
    }

    public AuthResponse recoveryPassword() {
        return null;
    }
}
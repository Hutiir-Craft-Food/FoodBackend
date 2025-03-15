package com.khutircraftubackend.auth;

import com.khutircraftubackend.auth.request.LoginRequest;
import com.khutircraftubackend.auth.request.RegisterRequest;
import com.khutircraftubackend.auth.response.AuthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Клас AuthenticationController обробляє запити, пов'язані з регістрацію и авторотізацію.
 */
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Operation(summary = "Authenticate user", description = "Authenticate user with email and password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse authenticateUser(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Login request containing email and password")
            @Valid @RequestBody LoginRequest loginRequest) {
        return authenticationService.authenticate(loginRequest);
    }

    @Operation(
            summary = "Register a new user",
            description = "Registration of a new user with the role of seller and buyer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = """
            Registration request containing user details. 

            - **SELLER**: Requires `details: { sellerName }`.
            - **BUYER**: Does not require `details`.
            """
    )
           @Valid @RequestBody RegisterRequest registerRequest) {
        return authenticationService.registerNewUser(registerRequest);
    }

    //TODO Need to update logic after design
    @Operation(summary = "Recover password", description = "Not implemented.!!!")
    @PostMapping("/recovery")
    public AuthResponse recoveryPassword() {
        return null;
    }
}
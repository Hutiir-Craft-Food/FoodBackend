package com.khutircraftubackend.auth;

import com.khutircraftubackend.auth.request.LoginRequest;
import com.khutircraftubackend.auth.request.RegisterRequest;
import com.khutircraftubackend.auth.response.AuthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * Клас AuthenticationController обробляє запити, пов'язані з реєстрацією та авторизацією.
 */
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Operation(summary = "Authenticate user",
            description = "Authenticate user with email and password.",
            tags = {"Authentication"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Validation error",
                    content = @Content( schema = @Schema(ref = "#/components/schemas/ErrorResponse400"))),
            @ApiResponse(responseCode = "401", description =
                    "Unauthorized - Помилка аутентифікації. Користувач з таким email заблокований",
                    content = @Content( schema = @Schema(ref = "#/components/schemas/ErrorResponseGeneric"))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Access Denied",
                    content = @Content( schema = @Schema(ref = "#/components/schemas/ErrorResponseGeneric")))
    })
    @PostMapping(value = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse authenticateUser(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Login request containing email and password")
            @Valid @RequestBody LoginRequest loginRequest) {
        return authenticationService.authenticate(loginRequest);
    }

    @Operation(
            summary = "Register a new user",
            description = "Registration of a new user with the role of seller and buyer.",
            tags = {"Authentication"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Validation error",
                    content = @Content( schema = @Schema(ref = "#/components/schemas/ErrorResponse400"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Помилка аутентифікації. Перевірте облікові данні",
                    content = @Content( schema = @Schema(ref = "#/components/schemas/ErrorResponseGeneric")))
    })
    @PostMapping(value = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User registration data",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = RegisterRequest.class),
                    examples = {
                            @ExampleObject(
                                    name = "Registration examples SELLER",
                                    value = """
                    {
                        "email": "seller@example.com",
                        "password": "Password123!",
                        "role": "SELLER",
                        "details": { "sellerName": "My Shop" },
                        "marketingConsent" : true
                    }
                    """
                            ),@ExampleObject(
                                    name = "Registration examples BUYER",
                                    value = """
                    {
                        "email": "buyer@example.com",
                        "password": "Password123!",
                        "role": "BUYER",
                        "marketingConsent" : true
                    }
                            """)
                    }
            )
    )
           @Valid @RequestBody RegisterRequest registerRequest) {
        return authenticationService.registerNewUser(registerRequest);
    }

    //TODO Need to update logic after design
    @Operation(summary = "Recover password",
            description = "Not implemented.!!!",
            hidden = true,
            tags = {"Authentication"})
    @PostMapping("/recovery")
    public AuthResponse recoveryPassword() {
        return null;
    }
}
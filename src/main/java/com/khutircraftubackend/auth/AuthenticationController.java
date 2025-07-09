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
import io.swagger.v3.oas.annotations.parameters.RequestBody;

/**
 * AuthenticationController обробляє запити, пов'язані з реєстрацією та авторизацією.
 */
public interface AuthenticationController {

    @Operation(summary = "Authenticate user",
            description = "Authenticate user with email and password.",
            tags = {"Authentication"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Validation error",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponse400"))),
            @ApiResponse(responseCode = "401", description =
                    "Unauthorized - Помилка аутентифікації. Користувач з таким email заблокований",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponseGeneric"))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Access Denied",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponseGeneric")))
    }) AuthResponse authenticateUser(@RequestBody(
            description = "Login request containing email and password") LoginRequest loginRequest);

    @Operation(
            summary = "Register a new user",
            description = "Registration of a new user with the role of seller and buyer.",
            tags = {"Authentication"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Validation error",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponse400"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Помилка аутентифікації. Перевірте облікові данні",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponseGeneric")))
    })
    AuthResponse register(@RequestBody(
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
                                            """),
                            @ExampleObject(
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
    ) RegisterRequest registerRequest);

    // TODO Need to update logic after design
    @Operation(summary = "Recover password",
            description = "Not implemented.!!!",
            hidden = true,
            tags = {"Authentication"})
    AuthResponse recoverPassword();
}

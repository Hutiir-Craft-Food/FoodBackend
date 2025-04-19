package com.khutircraftubackend.user;

import com.khutircraftubackend.confirm.ConfirmationRequest;
import com.khutircraftubackend.confirm.ConfirmationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.security.Principal;

public interface UserController {

    @Operation(summary = "Confirm user registration",
            description = "Confirm user registration with confirmation token.",
            tags = {"Authentication"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User confirmed successfully"),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request - Validation error.",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponse400"))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Access Denied",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponseGeneric")))
    })
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping(value = "/confirm",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    ConfirmationResponse confirmUser(@Valid @RequestBody ConfirmationRequest request, Principal principal);

    @Operation(
            summary = "Re-confirm email token",
            description = "Resend the email confirmation token for the authenticated user.",
            tags = {"Authentication"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Confirmation token resent successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponseGeneric"))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Access Denied",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponseGeneric")))
    })
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping("/re-confirm")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void reConfirmToken(Principal principal);

    //TODO Need update after design
    @Operation(summary = "Change password",
            description = "Not implemented.!!!",
            hidden = true,
            tags = {"Authentication"})
    @PostMapping("/password")
    @ResponseStatus(HttpStatus.OK)
    void updatePassword(Principal principal);
}

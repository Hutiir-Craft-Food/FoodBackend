package com.khutircraftubackend.user;

import com.khutircraftubackend.confirm.ConfirmationRequest;
import com.khutircraftubackend.confirm.ConfirmationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

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
    ConfirmationResponse confirmUser(ConfirmationRequest request, Principal principal);

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
    void reConfirmToken(Principal principal);

    //TODO Need update after design
    @Operation(summary = "Change password",
            description = "Not implemented.!!!",
            hidden = true,
            tags = {"Authentication"})
    void updatePassword(Principal principal);
}

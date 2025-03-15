package com.khutircraftubackend.user;

import com.khutircraftubackend.confirm.ConfirmationRequest;
import com.khutircraftubackend.confirm.ConfirmationResponse;
import com.khutircraftubackend.confirm.ConfirmationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping ("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final ConfirmationService confirmationService;

    @Operation(summary = "Confirm user registration", description = "Confirm user registration with confirmation token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User confirmed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid confirmation details")
    })
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping(value = "/confirm", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ConfirmationResponse confirmUser(@Valid @RequestBody ConfirmationRequest request, Principal principal) {
        return confirmationService.confirmToken(principal, request);
    }

    @Operation(
            summary = "Re-confirm email token",
            description = "Resend the email confirmation token for the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Confirmation token resent successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Too early to request a new token"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated")
    })
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping("/re-confirm")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reConfirmToken(Principal principal){
        confirmationService.reConfirmToken(principal);
    }

    //TODO Need update after design
    @Operation(summary = "Change password", description = "Not implemented.!!!")
    @PostMapping("/password")
    @ResponseStatus(HttpStatus.OK)
    public void updatePassword(Principal principal){
        return;
    }
}

package com.khutircraftubackend.user;

import com.khutircraftubackend.confirm.ConfirmationRequest;
import com.khutircraftubackend.confirm.ConfirmationResponse;
import com.khutircraftubackend.confirm.ConfirmationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Confirm user registration", description = "Confirm user registration with email and confirmation token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User confirmed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid confirmation details")
    })
    @PostMapping(value = "/confirm", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ConfirmationResponse confirmUser(@Valid @RequestBody ConfirmationRequest request) {
        return confirmationService.confirmToken(request);
    }

    @PostMapping("/re-confirm")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reConfirmToken(Principal principal){
        confirmationService.reConfirmToken(principal);
    }

    //TODO Need update after design
    @PostMapping("/password")
    @ResponseStatus(HttpStatus.OK)
    public void updatePassword(Principal principal){
        return;
    }
}

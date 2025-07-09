package com.khutircraftubackend.user;

import com.khutircraftubackend.confirm.ConfirmationRequest;
import com.khutircraftubackend.confirm.ConfirmationResponse;
import com.khutircraftubackend.confirm.ConfirmationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

    private final ConfirmationService confirmationService;

    @PostMapping(value = "/confirm",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ConfirmationResponse confirmUser(@Valid @RequestBody ConfirmationRequest request, Principal principal) {
        return confirmationService.confirmToken(principal, request);
    }

    @PostMapping("/re-confirm")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reConfirmToken(Principal principal) {
        confirmationService.reConfirmToken(principal);
    }

    @PostMapping("/password")
    @ResponseStatus(HttpStatus.OK)
    public void updatePassword(Principal principal) {
        // TBD
    }
}

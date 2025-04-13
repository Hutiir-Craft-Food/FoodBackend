package com.khutircraftubackend.user;

import com.khutircraftubackend.confirm.ConfirmationRequest;
import com.khutircraftubackend.confirm.ConfirmationResponse;
import com.khutircraftubackend.confirm.ConfirmationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController implements UserControllerApi {

    private final ConfirmationService confirmationService;

    public ConfirmationResponse confirmUser(@Valid @RequestBody ConfirmationRequest request, Principal principal) {
        return confirmationService.confirmToken(principal, request);
    }

    public void reConfirmToken(Principal principal) {
        confirmationService.reConfirmToken(principal);
    }

    public void updatePassword(Principal principal) {
        return;
    }
}

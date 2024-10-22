package com.khutircraftubackend.userprofile;

import com.khutircraftubackend.userprofile.request.ChangeOfEmailRequest;
import com.khutircraftubackend.userprofile.request.ConfirmEmailRequest;
import com.khutircraftubackend.userprofile.response.ChangeOfEmailResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("v1/profile")
@RequiredArgsConstructor
@Slf4j
public class UserProfileController {

    private final UserProfileService userProfileService;

    @PostMapping ("change-email")
    @ResponseStatus (HttpStatus.OK)
    public ChangeOfEmailResponse changeOfEmail (Principal principal, @Valid ChangeOfEmailRequest request){
        return userProfileService.changeOfEmail(principal, request);
    }

    @PostMapping ("confirm-email")
    @ResponseStatus (HttpStatus.NO_CONTENT)
    public void confirmEmail(Principal principal, @Valid ConfirmEmailRequest request){
        userProfileService.confirmEmail(principal, request);
    }
}

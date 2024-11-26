package com.khutircraftubackend.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping ("/v1/user")
@RequiredArgsConstructor
public class UserController {

    //TODO Need update after design
    @PostMapping("/password")
    @ResponseStatus(HttpStatus.OK)
    public void updatePassword(Principal principal){
        return;
    }
}

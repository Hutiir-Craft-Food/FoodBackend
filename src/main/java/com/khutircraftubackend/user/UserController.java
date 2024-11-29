package com.khutircraftubackend.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping ("/v1/users")
@RequiredArgsConstructor
public class UserController {


    //TODO Need update after design
    @PostMapping("/password")
    @ResponseStatus(HttpStatus.OK)
    public void updatePassword(Principal principal){
        return;
    }
}

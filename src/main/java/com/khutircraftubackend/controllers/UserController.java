package com.khutircraftubackend.controllers;

import com.khutircraftubackend.dto.UserDTO;
import com.khutircraftubackend.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody UserDTO userDTO) {
        UserDTO registeredUser = userService.registerNewUser(userDTO);
        return ResponseEntity.ok(registeredUser);
    }

    @GetMapping("/confirm")
    public ResponseEntity<Void> confirmUser(@RequestParam String email) {
        userService.enableUser(email);
        return ResponseEntity.ok().build();
    }
}

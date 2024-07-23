package com.khutircraftubackend.controllers;

import com.khutircraftubackend.dto.UserDTO;
import com.khutircraftubackend.dto.security.JwtResponse;
import com.khutircraftubackend.dto.security.LoginRequest;
import com.khutircraftubackend.dto.security.PasswordRecoveryRequest;
import com.khutircraftubackend.dto.security.PasswordUpdateRequest;
import com.khutircraftubackend.security.JwtUtils;
import com.khutircraftubackend.services.PersonDetailsServices;
import com.khutircraftubackend.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Клас UserController обробляє запити, пов'язані з користувачами.
 */

@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PersonDetailsServices personDetailsServices;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticate(loginRequest.getEmail(), loginRequest.getPassword());

        if(authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtUtils.generateJwtToken(userDetails.getUsername());

            return ResponseEntity.ok(new JwtResponse(jwt));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    private Authentication authenticate(String email, String password) {
        UserDetails userDetails = personDetailsServices.loadUserByUsername(email);

        if(userDetails != null && passwordEncoder.matches(password, userDetails.getPassword())) {
            return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
        } else {
            return null;
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody UserDTO userDTO) {
        UserDTO registeredUser = userService.registerNewUser(userDTO);
        return ResponseEntity.ok(registeredUser);
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody PasswordUpdateRequest passwordUpdateRequest) {

        boolean isUpdated = userService.updatePassword(passwordUpdateRequest);
        if(isUpdated) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/recovery")
    public ResponseEntity<Void> recoverPassword(@Valid @RequestBody PasswordRecoveryRequest passwordRecoveryRequest) {
        userService.initiatePasswordRecovery(passwordRecoveryRequest.getEmail());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/confirm")
    public ResponseEntity<Void> confirmUser(@RequestParam String email) {
        userService.enableUser(email);
        return ResponseEntity.ok().build();
    }
}

package com.khutircraftubackend.auth;

import com.khutircraftubackend.auth.request.LoginRequest;
import com.khutircraftubackend.auth.request.RegisterRequest;
import com.khutircraftubackend.jwtToken.JwtResponse;
import com.khutircraftubackend.jwtToken.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

/**
 * Клас AuthenticationController обробляє запити, пов'язані з користувачами.
 */
@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final JwtUtils jwtUtils;

    /**
     * Authenticate user response entity.
     *
     * @param loginRequest the login request
     * @return the response entity
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            authenticationService.authenticate(loginRequest);
            String jwt = jwtUtils.generateJwtToken(loginRequest.email());

            return ResponseEntity.ok(new JwtResponse(jwt));
        } catch (BadCredentialsException e) {
            log.error("Login failed for email: {}, invalid password", loginRequest.email());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (RuntimeException e) {
            log.error("Login failed for email: {}", loginRequest.email(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
authenticationService.registerNewUser(request);
return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully. Please check your email for the confirmation code.");
    }

    @GetMapping ("/confirm")
    public ResponseEntity<String> confirmUser(@RequestParam String email, @RequestParam("token") String token) {
        authenticationService.confirmUser(email, token);
    return ResponseEntity.ok("User confirmed successfully.");
    }

    @PostMapping("/update-token")
    public ResponseEntity<String> updateJwtToken(@RequestParam String email) {
        authenticationService.updateJwtToken(email);
        return ResponseEntity.ok("JWT token updated successfully");
    }

    /**
     * Update password response entity.
     *
     * @param passwordUpdateRequest the password update request
     * @return the response entity
     */
//    @PatchMapping("/password")
//    public ResponseEntity<Void> updatePassword(@Valid @RequestBody PasswordUpdateRequest passwordUpdateRequest) {
//
//        boolean isUpdated = authenticationService.updatePassword(passwordUpdateRequest);
//        if(isUpdated) {
//            return ResponseEntity.ok().build();
//        } else {
//            return ResponseEntity.badRequest().build();
//        }
//    }

    /**
     * Recover password response entity.
     *
     * @param passwordRecoveryRequest the password recovery request
     * @return the response entity
     */
//    @PostMapping("/recovery")
//    public ResponseEntity<Void> recoverPassword(@Valid @RequestBody PasswordRecoveryRequest passwordRecoveryRequest) {
//        userService.initiatePasswordRecovery(passwordRecoveryRequest.email());
//        return ResponseEntity.ok().build();
//    }
}

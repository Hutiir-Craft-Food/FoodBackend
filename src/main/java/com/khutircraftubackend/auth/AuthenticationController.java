package com.khutircraftubackend.auth;

import com.khutircraftubackend.auth.exception.UserExistsException;
import com.khutircraftubackend.auth.request.ConfirmationRequest;
import com.khutircraftubackend.auth.request.LoginRequest;
import com.khutircraftubackend.auth.request.RegisterRequest;
import com.khutircraftubackend.auth.response.AuthResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Клас AuthenticationController обробляє запити, пов'язані з користувачами.
 */
@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    /**
     * Authenticate user response entity.
     *
     * @param loginRequest the login request
     * @return the response entity
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse authResponse = authenticationService.authenticate(loginRequest);
            return ResponseEntity.ok(authResponse);
        } catch (BadCredentialsException e) {
            log.error("Login failed for email: {}, invalid password", loginRequest.email());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (RuntimeException e) {
            log.error("Login failed for email: {}", loginRequest.email(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected exception while logging in: {}", e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            AuthResponse authResponse = authenticationService.registerNewUser(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
        } catch (UserExistsException e) {
            log.error("Login failed for email: {}", registerRequest.email(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected exception while logging in: {}", e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirmUser(@RequestBody ConfirmationRequest confirmationRequest) {
        try {
            authenticationService.confirmUser(confirmationRequest.email(), confirmationRequest.jwt());
            return ResponseEntity.ok("User confirmed successfully.");
        } catch (IllegalArgumentException e) {
            // 400 Bad Request
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }
}

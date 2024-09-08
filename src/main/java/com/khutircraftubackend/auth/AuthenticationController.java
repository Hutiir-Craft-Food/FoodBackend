package com.khutircraftubackend.auth;

import com.khutircraftubackend.auth.request.LoginRequest;
import com.khutircraftubackend.auth.request.RegisterRequest;
import com.khutircraftubackend.auth.response.AuthResponse;
import com.khutircraftubackend.auth.security.PasswordUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

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
    @Operation(summary = "Authenticate user", description = "Authenticate user with email and password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Parameter(description = "Login request containing email and password") @Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = authenticationService.authenticate(loginRequest);
        return ResponseEntity.ok(authResponse);
    }

    @Operation(summary = "Register a new user", description = "Register a new user with email and password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "401", description = "User already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@Parameter(description = "Registration request containing user details") @Valid @RequestBody RegisterRequest registerRequest) {
        authenticationService.registerNewUser(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Завершіть реєстрацію підтвердженням з переходом на пошту");
    }
    //TODO Need update swagger
    @Operation(summary = "Confirm user registration", description = "Confirm user registration with email and confirmation token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User confirmed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid confirmation details")
    })
    @GetMapping("/confirm")
    public ResponseEntity<String> confirmUser(@RequestParam String email, @RequestParam String key) {
        authenticationService.confirmUser(email, key);
        return ResponseEntity.ok("User confirmed successfully.");
    }

    @Operation(summary = "Update user password", description = "Update password with a valid authorization token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password updated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PatchMapping("/update-password")
    public ResponseEntity<String> updatePassword(@Parameter(description = "Password update request containing new password")
                                                 @Valid @RequestBody PasswordUpdateRequest passwordUpdateRequest,
                                                 Principal principal) {
        authenticationService.updatePassword(principal, passwordUpdateRequest);
        return ResponseEntity.ok("Password updated successfully");
    }

    @Operation(summary = "Recover user password", description = "Recover password with a valid authorization token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password recovery initiated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })

    //TODO Need to update logic after design
    @PostMapping("/recovery-password")
    public ResponseEntity<String> recoveryPassword(Principal principal) {
        authenticationService.recoveryPassword(principal);
        return ResponseEntity.ok("Password recovery successfully " + "\n" +
                " go to email to get new password and change password " +
                "to yours when logging in for security");
    }
}

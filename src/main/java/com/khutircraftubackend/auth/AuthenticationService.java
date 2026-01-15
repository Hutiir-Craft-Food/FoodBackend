package com.khutircraftubackend.auth;

import com.khutircraftubackend.auth.exception.InvalidCredentialsException;
import com.khutircraftubackend.auth.exception.RegistrationException;
import com.khutircraftubackend.auth.exception.UserBlockedException;
import com.khutircraftubackend.auth.request.LoginRequest;
import com.khutircraftubackend.auth.request.RegisterRequest;
import com.khutircraftubackend.auth.response.AuthResponse;
import com.khutircraftubackend.confirm.ConfirmationService;
import com.khutircraftubackend.jwt.JwtUtils;
import com.khutircraftubackend.mail.EmailSender;
import com.khutircraftubackend.marketing.MarketingCampaignService;
import com.khutircraftubackend.seller.SellerService;
import com.khutircraftubackend.user.UserEntity;
import com.khutircraftubackend.user.UserRepository;
import com.khutircraftubackend.user.UserService;
import jakarta.persistence.PostUpdate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import static com.khutircraftubackend.auth.exception.AuthResponseMessages.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    
    private final UserRepository userRepository;
    private final MarketingCampaignService marketingCampaignService;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final ConfirmationService confirmationService;
    private final SellerService sellerService;
    private final EmailSender emailSender;
    private final AuthenticationManager authenticationManager;
    
    
    @Transactional
    public AuthResponse authenticate(LoginRequest request) {
        
        UserEntity user = userService.findByEmail(request.email());
        
        try {
            authenticateUser(request.email(), request.password());
        } catch (BadCredentialsException ex) {
            log.warn("Authentication failed: bad credentials, email={}", request.email());
            throw new InvalidCredentialsException(AUTH_INVALID_CREDENTIALS);
        }
        
        if (Boolean.FALSE.equals(user.isEnabled())) {
            log.warn("Access denied for blocked user, email={}", request.email());
            throw new UserBlockedException(AUTH_USER_BLOCKED);
        }
        
        return AuthResponse.builder()
                .jwt(jwtUtils.generateJwtToken(user.getEmail()))
                .role(user.getRole())
                .confirmed(user.isConfirmed())
                .build();
    }
    
    private void authenticateUser(String email, String password) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);
        authenticationManager.authenticate(authenticationToken);
    }
    
    @Transactional
    @PostUpdate
    public AuthResponse registerNewUser(RegisterRequest request) {
        
        if (userRepository.existsByEmail(request.email())) {
            emailSender.sendSimpleMessage(request.email(),
                    AUTH_CODE_SUBJECT,
                    AUTH_CODE_TEXT);
            throw new RegistrationException(REGISTRATION_INVALID_REQUEST);
        }
        
        UserEntity user = userService.createdUser(request);
        confirmationService.sendVerificationEmail(user, false);
        marketingCampaignService.createReceiveAdvertising(request, user);
        
        if (userService.isSeller(user)) {
            sellerService.createSeller(request, user);
        }
        
        return AuthResponse.builder()
                .jwt(jwtUtils.generateJwtToken(user.getEmail()))
                .role(user.getRole())
                .confirmed(user.isConfirmed())
                .build();
    }
    
}
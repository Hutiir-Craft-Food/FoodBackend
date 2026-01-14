package com.khutircraftubackend.auth;

import com.khutircraftubackend.auth.exception.AuthenticationException;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.khutircraftubackend.auth.AuthResponseMessages.*;

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
    private final PasswordEncoder passwordEncoder;
    
    
    @Transactional
    public AuthResponse authenticate(LoginRequest request) {
        
        UserEntity user = userService.findByEmail(request.email());
        
        if (user == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
            log.warn("Authentication failed: invalid credentials, email={}", request.email());
            throw new BadCredentialsException(INVALID_CREDENTIALS_PUBLIC);
        }
        
        if (Boolean.FALSE.equals(user.isEnabled())) {
            log.warn("Access denied for blocked user, email={}", request.email());
            throw new UserBlockedException(USER_BLOCKED_PUBLIC);
        }
        
        return AuthResponse.builder()
                .jwt(jwtUtils.generateJwtToken(user.getEmail()))
                .role(user.getRole())
                .confirmed(user.isConfirmed())
                .build();
    }
    
    @Transactional
    @PostUpdate
    public AuthResponse registerNewUser(RegisterRequest request) {
        
        if (userRepository.existsByEmail(request.email())) {
            emailSender.sendSimpleMessage(request.email(),
                    AuthResponseMessages.AUTH_CODE_SUBJECT,
                    String.format(
                            AuthResponseMessages.AUTH_CODE_TEXT));
            throw new AuthenticationException(USER_EXISTS);
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
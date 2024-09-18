package com.khutircraftubackend.auth;

import com.khutircraftubackend.auth.request.LoginRequest;
import com.khutircraftubackend.auth.response.AuthResponse;
import com.khutircraftubackend.exception.user.BadCredentialsException;
import com.khutircraftubackend.exception.user.UserNotFoundException;
import com.khutircraftubackend.jwtToken.JwtUtils;
import com.khutircraftubackend.mail.EmailSender;
import com.khutircraftubackend.marketing.MarketingCampaignRepository;
import com.khutircraftubackend.seller.SellerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertNull;

@ExtendWith(MockitoExtension.class)
@Transactional
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private SellerRepository sellerRepository;
    @Mock
    private MarketingCampaignRepository marketingCampaignRepository;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EmailSender emailSender;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserService userService;
    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    @Rollback
    void testAuthenticateSuccess() {

        LoginRequest request =new LoginRequest( "test@test.com", "testpassword");

        UserEntity expectedUser = UserEntity.builder()
                .email(request.email())
                .password(request.password())
                .enabled(true)
                .role(Role.SELLER)
                .build();

        when(userService.findByEmail(request.email())).thenReturn(expectedUser);
        when(jwtUtils.generateJwtToken(expectedUser.getEmail())).thenReturn("mocked-jwt-token");

        AuthResponse result = authenticationService.authenticate(request);

        assertEquals("mocked-jwt-token", result.jwt());
        assertEquals(request.email(), result.email());
        assertEquals(AuthResponseMessages.USER_ENABLED, result.message());

        verify(userService).findByEmail(request.email());
        verify(jwtUtils).generateJwtToken(expectedUser.getEmail());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @Rollback
    void testAuthenticateEmailNotFound(){

        LoginRequest request = new LoginRequest("test@test", "test");

        when(userService.findByEmail(request.email())).thenThrow(new UserNotFoundException("User not found"));

        assertThrows(UserNotFoundException.class, () -> authenticationService.authenticate(request));
        verify(userService, times(1)).findByEmail(request.email());
    }

    @Test
    @Rollback
    void testAuthenticateUserNotEnabled(){

        LoginRequest request = new LoginRequest("test@test", "test");

        UserEntity expectedUser = UserEntity.builder()
                .email(request.email())
                .password(request.password())
                .enabled(false)
                .role(Role.SELLER)
                .build();

        when(userService.findByEmail(request.email())).thenReturn(expectedUser);

        AuthResponse result = authenticationService.authenticate(request);
        boolean isResult = expectedUser.isEnabled();

        assertEquals(result.message(),
                String.format(AuthResponseMessages.USER_BLOCKED, request.email()));
        assertEquals(isResult, expectedUser.isEnabled());
        assertNull(null, result.jwt());

        verify(userService, times(1)).findByEmail(request.email());
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    @Rollback
    void testAuthenticateUserWithInvalidPassword() {
        LoginRequest request = new LoginRequest("test@test", "invalid_password");

        UserEntity expectedUser = UserEntity.builder()
                .email(request.email())
                .password("correct_password")
                .enabled(true)
                .role(Role.SELLER)
                .build();

        when(userService.findByEmail(request.email())).thenReturn(expectedUser);

        doThrow(new BadCredentialsException("Invalid credentials"))
                .when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThrows(BadCredentialsException.class, () ->
            authenticationService.authenticate(request)
        );

        verify(userService, times(1)).findByEmail(request.email());
        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
    }


    @Test
    void registerNewUser() {
    }

    @Test
    void confirmUser() {
    }

    @Test
    void updatePassword() {
    }

    @Test
    void getUserForPrincipal() {
    }
}
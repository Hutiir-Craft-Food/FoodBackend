package com.khutircraftubackend.auth;

import com.khutircraftubackend.user.exception.UserBlockedException;
import com.khutircraftubackend.confirm.ConfirmService;
import com.khutircraftubackend.auth.request.LoginRequest;
import com.khutircraftubackend.auth.request.RegisterRequest;
import com.khutircraftubackend.auth.response.AuthResponse;
import com.khutircraftubackend.jwt.JwtUtils;
import com.khutircraftubackend.marketing.MarketingCampaignService;
import com.khutircraftubackend.seller.SellerService;
import com.khutircraftubackend.user.Role;
import com.khutircraftubackend.user.UserEntity;
import com.khutircraftubackend.user.UserRepository;
import com.khutircraftubackend.user.UserService;
import com.khutircraftubackend.auth.exception.UserExistsException;
import jakarta.persistence.PostUpdate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

/**
 * Клас AuthenticationService реалізує бізнес-логіку для роботи з користувачами.
 * <p>
 * Цей клас містить методи для створення, оновлення, видалення та отримання користувачів.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final MarketingCampaignService marketingCampaignService;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final ConfirmService confirmService;
    private final SellerService sellerService;

    /**
     * Аутентифікує користувача за вказаними email та паролем.
     *
     * @param request Запит на авторизацію, що містить email та пароль.
     * @return Об'єкт AuthResponse з JWT токеном та email користувача.
     * @throws BadCredentialsException Якщо вказані невірні дані для входу.
     * @throws AuthenticationException Якщо виникла непередбачена помилка під час аутентифікації.
     */

    @Transactional
    public AuthResponse authenticate(LoginRequest request) {

        UserEntity user = userService.findByEmail(request.email());
        if (Boolean.FALSE.equals(user.isEnabled())) {
            String message = String.format(AuthResponseMessages.USER_BLOCKED, user.getEmail());
            // TODO: consider adding some logging here
            throw new UserBlockedException(message);
        }

        authenticateUser(request.email(), request.password());

        return AuthResponse.builder()
                .jwt(jwtUtils.generateJwtToken(user.getEmail()))
                .role(user.getRole())
                .confirmed(user.isConfirmed())
                .build();
    }

    private void authenticateUser(String email, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        authenticationManager.authenticate(authenticationToken);
    }

    @Transactional
    @PostUpdate
    public AuthResponse registerNewUser(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            // TODO: consider adding some logging here

            // TODO:
            //  do we really want to let an attacker know that
            //  the email they are trying already exists and so it might be a valid email?
            //  do we really want to expose registered emails?
            //  some thoughts about it here:
            //      https://security.stackexchange.com/a/51857
            throw new UserExistsException(AuthResponseMessages.EMAIL_IS_ALREADY_IN_USE);
        }

        UserEntity user = userService.createdUser(request);
        confirmService.sendVerificationEmail(user, false);
        marketingCampaignService.createReceiveAdvertising(request, user);

        // TODO: we should pass User instead of its Role.
        if (isSeller(request.role())) {
            sellerService.createSeller(request, user);
        }

        return AuthResponse.builder()
                .jwt(jwtUtils.generateJwtToken(user.getEmail()))
                .role(user.getRole())
                .confirmed(user.isConfirmed())
                .build();
    }

    // TODO: if we really need this method,
    //  then it should accept User as parameter, instead or Role.
    private boolean isSeller(Role role) {
        return role.equals(Role.SELLER);
    }

}
package com.khutircraftubackend.auth;

import com.khutircraftubackend.auth.request.ConfirmUserRequest;
import com.khutircraftubackend.auth.exception.user.UserExistsException;
import com.khutircraftubackend.auth.request.LoginRequest;
import com.khutircraftubackend.auth.request.RegisterRequest;
import com.khutircraftubackend.auth.response.AuthResponse;
import com.khutircraftubackend.auth.security.PasswordUpdateRequest;
import com.khutircraftubackend.jwt.JwtUtils;
import com.khutircraftubackend.mail.EmailSender;
import com.khutircraftubackend.marketing.MarketingCampaignEntity;
import com.khutircraftubackend.marketing.MarketingCampaignRepository;
import com.khutircraftubackend.seller.SellerEntity;
import com.khutircraftubackend.seller.SellerRepository;
import jakarta.persistence.PostUpdate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Random;
import java.util.UUID;

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
    private final SellerRepository sellerRepository;
    private final MarketingCampaignRepository marketingCampaignRepository;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final EmailSender emailSender;
    private final AuthenticationManager authenticationManager;
    private final Random random = new Random();

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
        if (!user.isEnabled()) {
            return buildResponseForBlockedUser(user);
        }

        authenticateUser(request.email(), request.password());
        String token = jwtUtils.generateJwtToken(user.getEmail());
        return buildResponseForAllowedUser(user, token);
    }

    private void authenticateUser(String email, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        authenticationManager.authenticate(authenticationToken);
    }

    private AuthResponse buildResponseForBlockedUser(UserEntity user) {
        return AuthResponse
                .builder()
                .message(String.format(AuthResponseMessages.USER_BLOCKED, user.getEmail()))
                .build();
    }

    private AuthResponse buildResponseForAllowedUser(UserEntity user, String token) {
        return AuthResponse.builder()
                .jwt(token)
                .email(user.getEmail())
                .message(String.format(AuthResponseMessages.USER_ENABLED))
                .build();
    }


    @Transactional
    @PostUpdate
    public AuthResponse registerNewUser(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new UserExistsException(AuthResponseMessages.EMAIL_IS_ALREADY_IN_USE);
        }

        UserEntity user = buildAndSaveUser(request);
        sendVerificationEmail(user);
        createReceiveAdvertising(request, user);

        if (isSeller(request.role())) {
            createSeller(request, user);
        }

        return AuthResponse.builder()
                .build();
    }

    private boolean isSeller(Role role) {
        return role.equals(Role.SELLER);
    }

    private UserEntity buildAndSaveUser(RegisterRequest request) {
        UserEntity user = UserEntity.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .enabled(false)
                .role(request.role())
                .build();
        userRepository.saveAndFlush(user);
        return user;
    }

    private void createReceiveAdvertising(RegisterRequest request, UserEntity user){
        MarketingCampaignEntity advertising = MarketingCampaignEntity
                .builder()
                .user(user)
                .isSubscribed(request.isReceiveAdvertising())
                .build();
        marketingCampaignRepository.save(advertising);
    }

    private void createSeller(RegisterRequest request, UserEntity user) {
        SellerEntity seller = SellerEntity
                .builder()
                .sellerName(request.details().sellerName())
                .companyName(request.details().companyName())
                .phoneNumber(request.details().phoneNumber())
                .user(user)
                .build();
        sellerRepository.save(seller);
    }

    private void sendVerificationEmail(UserEntity user) {
        String verificationCode = getRandomNumber();
        emailSender.sendSimpleMessage(user.getEmail(),
                AuthResponseMessages.VERIFICATION_CODE_SUBJECT,
                String.format(
                        AuthResponseMessages.VERIFICATION_CODE_TEXT, verificationCode));
        user.setConfirmationToken(verificationCode);
        userRepository.save(user);
    }

    public String getRandomNumber(){
        int randomNumber = 100000 + random.nextInt(900000);
        return String.valueOf(randomNumber);
    }

    @Transactional
    public void confirmUser(ConfirmUserRequest request) {
        UserEntity user = userService.findByEmail(request.email());
        validateConfirmationToken(request.confirmationToken(), user);
        updateConfirmationToken(user);
    }

    private void validateConfirmationToken(String key, UserEntity user) {
        if (Boolean.FALSE.equals(
                key.equals(user.getConfirmationToken()))) {
            throw new IllegalArgumentException(AuthResponseMessages.NOT_VALID_CONFIRMATION_TOKEN);
        }
    }

    private void updateConfirmationToken(UserEntity user) {
        user.setEnabled(true);
        user.setConfirmationToken(null);
        userRepository.save(user);
    }

    @Transactional
    public void updatePassword(Principal principal, PasswordUpdateRequest passwordUpdateRequest) {
        UserEntity user = userService.findByPrincipal(principal);
        user.setPassword(passwordEncoder.encode(passwordUpdateRequest.password()));
        userRepository.save(user);
    }

    @Transactional
    public void recoveryPassword(Principal principal) {

        // Генеруємо новий тимчасовий пароль
        String temporaryPassword = generateTemporaryPassword();
        String encodedPassword = passwordEncoder.encode(temporaryPassword);

        // Оновлюємо пароль у базі даних
        UserEntity user = userService.findByPrincipal(principal);
        user.setPassword(encodedPassword);
        userRepository.save(user);

        // Відправляємо тимчасовий пароль на email користувача
        emailSender.sendSimpleMessage(user.getEmail(), AuthResponseMessages.RECOVERY_PASSWORD_SUBJECT,
                String.format(AuthResponseMessages.RECOVERY_PASSWORD_TEXT, temporaryPassword));

    }

    private String generateTemporaryPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

}



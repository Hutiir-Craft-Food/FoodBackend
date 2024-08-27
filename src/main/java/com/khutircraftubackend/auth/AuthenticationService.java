package com.khutircraftubackend.auth;

import com.auth0.jwt.JWTVerifier;
import com.khutircraftubackend.auth.request.ConfirmationRequest;
import com.khutircraftubackend.exception.user.UserExistsException;
import com.khutircraftubackend.exception.user.UserNotFoundException;
import com.khutircraftubackend.auth.request.LoginRequest;
import com.khutircraftubackend.auth.request.RegisterRequest;
import com.khutircraftubackend.auth.response.AuthResponse;
import com.khutircraftubackend.auth.security.PasswordUpdateRequest;
import com.khutircraftubackend.jwtToken.JwtUtils;
import com.khutircraftubackend.mail.EmailSender;
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
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final EmailSender emailSender;
    private final AuthenticationManager authenticationManager;
    private final JWTVerifier jwtVerifier;
    private final UserDetailsServiceImpl userDetailsServices;
    private final SellerRepository sellerRepository;

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

        UserEntity user = getUserForEmail(request.email());
        if (!user.isEnabled()) {
            return buildDisabledUser(user);
        }

        authenticateUser(request.email(), request.password());
        String token = jwtUtils.generateJwtToken(user.getEmail());
        return buildEnabledUser(user, token);
    }

    private UserEntity getUserForEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(String.format(FinalMessagesUsers.EMAIL_NOT_FOUND, email)));
    }

    private void authenticateUser(String email, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        authenticationManager.authenticate(authenticationToken);
    }

    private AuthResponse buildDisabledUser(UserEntity user) {
        return AuthResponse
                .builder()
                .message(String.format(FinalMessagesUsers.EMAIL_DISABLED, user.getEmail()))
                .build();
    }

    private AuthResponse buildEnabledUser(UserEntity user, String token) {
        return AuthResponse.builder()
                .jwt(token)
                .email(user.getEmail())
                .message(String.format(FinalMessagesUsers.EMAIL_ENABLED))
                .build();
    }

    @Transactional
    @PostUpdate
    public AuthResponse registerNewUser(RegisterRequest request) {

        validateEmailNotInUse(request.email());
        UserEntity user = buildAndSaveUser(request);
        sendVerificationEmail(user);

        if (isSeller(request.role())) {
            createSeller(request, user);
        }

        //TODO consider returning token at this point
        return AuthResponse.builder()
                .build();
    }

    private boolean isSeller(Role role) {
        return "SELLER".equals(role);
    }

    private UserEntity buildAndSaveUser(RegisterRequest request) {
        UserEntity user = UserEntity.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .enabled(false)
                // TODO: request this from Controller
                .role(request.role())
                .build();
        userRepository.saveAndFlush(user);
        return user;
    }

    private void createSeller(RegisterRequest request, UserEntity user) {
        SellerEntity seller = SellerEntity
                .builder()
                .sellerName(request.details().sellerName())
                .companyName(request.details().companyName())
                .user(user)
                .build();
        sellerRepository.save(seller);
    }

    private void validateEmailNotInUse(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserExistsException(FinalMessagesUsers.EMAIL_IS_ALREADY_IN_USE);
        }
    }

    private void sendVerificationEmail(UserEntity user) {
        String verificationCode = UUID.randomUUID().toString();
        emailSender.sendSimpleMessage(user.getEmail(),
                FinalMessagesUsers.VERIFICATION_CODE_SUBJECT,
                String.format(FinalMessagesUsers.VERIFICATION_CODE_TEXT, verificationCode));
        user.setConfirmationToken(verificationCode);
        userRepository.save(user);
    }

    @Transactional
    public void confirmUser(ConfirmationRequest request) {
        UserEntity user = getUserForEmail(request.email());
        validateConfirmationToken(request, user);
        updateConfirmationToken(user);
    }

    private void validateConfirmationToken(ConfirmationRequest request, UserEntity user) {
        if (Boolean.FALSE.equals(
                request.confirmationToken().equals(user.getConfirmationToken()))) {
            throw new IllegalArgumentException(FinalMessagesUsers.NOT_VALID_CONFIRMATION_TOKEN);
        }
    }

    private void updateConfirmationToken(UserEntity user) {
        user.setEnabled(true);
        user.setConfirmationToken(null);

        // TODO: Оновлення ролі користувача
        userRepository.save(user);
    }

    @Transactional
    public void updatePassword(Principal principal, PasswordUpdateRequest passwordUpdateRequest) {
        UserEntity user = getUserForPrincipal(principal);
        user.setPassword(passwordEncoder.encode(passwordUpdateRequest.newPassword()));
        userRepository.save(user);
    }

    private UserEntity getUserForPrincipal(Principal principal) {
        String email = principal.getName();
        return getUserForEmail(email);
    }

    @Transactional
    public void recoveryPassword(Principal principal) {

        // Генеруємо новий тимчасовий пароль
        String temporaryPassword = generateTemporaryPassword();
        String encodedPassword = passwordEncoder.encode(temporaryPassword);

        // Оновлюємо пароль у базі даних
        UserEntity user = getUserForPrincipal(principal);
        user.setPassword(encodedPassword);
        userRepository.save(user);

        // Відправляємо тимчасовий пароль на email користувача
        emailSender.sendSimpleMessage(user.getEmail(), FinalMessagesUsers.RECOVERY_PASSWORD_SUBJECT,
                String.format(FinalMessagesUsers.RECOVERY_PASSWORD_TEXT, temporaryPassword));

    }

    private String generateTemporaryPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

}



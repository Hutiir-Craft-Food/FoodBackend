package com.khutircraftubackend.auth;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.khutircraftubackend.exception.user.UserExistsException;
import com.khutircraftubackend.exception.user.UserNotFoundException;
import com.khutircraftubackend.auth.request.LoginRequest;
import com.khutircraftubackend.auth.request.RegisterRequest;
import com.khutircraftubackend.auth.response.AuthResponse;
import com.khutircraftubackend.auth.security.PasswordRecoveryRequest;
import com.khutircraftubackend.auth.security.PasswordUpdateRequest;
import com.khutircraftubackend.jwtToken.JwtUtils;
import com.khutircraftubackend.mail.EmailSender;
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
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password())
        );

        // Fetch user by email
        UserEntity user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException("Користувач з таким email не знайдений"));

        if (Boolean.FALSE.equals(user.isEnabled())) {
            return AuthResponse
                    .builder()
                    .message("Підтвердження за поштою не пройдено")
                    .build();
        }
        String jwt = jwtUtils.generateJwtToken(user.getEmail());
        return AuthResponse.builder()
                .jwt(jwt)
                .email(user.getEmail())
                .message("Вітаємо з успішним входом")
                .build();
    }

    @Transactional
    @PostUpdate
    public AuthResponse registerNewUser(RegisterRequest request) {
        if(userRepository.existsByEmail(request.email())) {
            throw new UserExistsException("Цей email зайнятий, використайте інший або ввійдіть під цим");
        }

        UserEntity user = UserEntity.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .enabled(false)
                // TODO: request this from Controller
                .role(Role.GUEST)
                .build();

        userRepository.saveAndFlush(user);

        String verificationCode = UUID.randomUUID().toString();
        emailSender.sendSimpleMessage(user.getEmail(), "Підтвердження реєстрації",
                "Будь ласка, підтвердіть вашу реєстрацію за посиланням: " + verificationCode);

        user.setConfirmationToken(verificationCode);
        userRepository.save(user);

        //TODO consider returning token at this point
        return AuthResponse.builder()
                .build();
    }

    @Transactional
    public void confirmUser(String email, String confirmationToken) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Користувач з таким email не знайдений"));

        if (Boolean.FALSE.equals(!confirmationToken.equals(user.getConfirmationToken()))) {
            throw new IllegalArgumentException("Неправильний код підтвердження.");
        }

        user.setEnabled(true);
        user.setConfirmationToken(null);

        // TODO: Оновлення ролі користувача
        userRepository.save(user);
    }

    @Transactional
    public void updatePassword(String jwt, PasswordUpdateRequest passwordUpdateRequest) {
        DecodedJWT decodedJWT = jwtVerifier.verify(jwt);
        String emailFromToken = decodedJWT.getSubject();
        if(Boolean.FALSE.equals(emailFromToken.equals(passwordUpdateRequest.email()))) {
            throw new UserNotFoundException("Цей токен не належить цьому користувачу.");
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsServices.loadUserByUsername(emailFromToken);
        UserEntity user = userDetails.getUser();

        String encodeNewPassword = passwordEncoder.encode(passwordUpdateRequest.newPassword());
        user.setPassword(encodeNewPassword);
        userRepository.save(user);
    }
    @Transactional
    public void recoveryPassword(String jwt, PasswordRecoveryRequest passwordRecoveryRequest) {
        DecodedJWT decodedJWT = jwtVerifier.verify(jwt);
        String emailFromToken = decodedJWT.getSubject();
        if (Boolean.FALSE.equals(emailFromToken.equals(passwordRecoveryRequest.email()))) {
            throw new UserNotFoundException("Цей токен не належить цьому користувачу.");
        }

        // Генеруємо новий тимчасовий пароль
        String temporaryPassword = generateTemporaryPassword();
        String encodedPassword = passwordEncoder.encode(temporaryPassword);

        // Оновлюємо пароль у базі даних
        UserEntity user = userRepository.findByEmail(emailFromToken)
                .orElseThrow(() -> new UserNotFoundException("Користувач з таким email не знайдений " + emailFromToken));
        user.setPassword(encodedPassword);
        userRepository.save(user);

        // Відправляємо тимчасовий пароль на email користувача
        emailSender.sendSimpleMessage(passwordRecoveryRequest.email(), "Відновлення паролю",
                "Ваш тимчасовий пароль: " + temporaryPassword);

    }
    private String generateTemporaryPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

}



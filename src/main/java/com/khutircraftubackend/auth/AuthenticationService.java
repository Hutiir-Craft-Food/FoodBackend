package com.khutircraftubackend.auth;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.khutircraftubackend.auth.exception.UserExistsException;
import com.khutircraftubackend.auth.exception.UserNotFoundException;
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
    private final UserDetailsServicesImpl userDetailsServices;

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

        // Fetch userEntity by email
        UserEntity userEntity = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException("Користувач з таким email не знайдений"));

        String jwt = jwtUtils.generateJwtToken(userEntity.getEmail());
        return AuthResponse.builder()
                .jwt(jwt)
                .email(userEntity.getEmail())
                .build();
    }

    @Transactional
    @PostUpdate
    public AuthResponse registerNewUser(RegisterRequest request) {
        if(userRepository.existsByEmail(request.email())) {
            throw new UserExistsException("Цей email зайнятий, використайте інший або ввійдіть під цим");
        }

        UserEntity userEntity = UserEntity.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .enabled(false)
                // TODO: request this from Controller
                .role(Role.GUEST)
                .build();

        userRepository.saveAndFlush(userEntity);

        String verificationCode = UUID.randomUUID().toString();
        String confirmationLink = "http://localhost:8080/confirm?token=" + verificationCode;
        emailSender.sendSimpleMessage(userEntity.getEmail(), "Підтвердження реєстрації",
                "Будь ласка, підтвердіть вашу реєстрацію за посиланням: " + confirmationLink);

        userEntity.setConfirmationToken(verificationCode);
        userRepository.save(userEntity);

        String jwt = jwtUtils.generateJwtToken(userEntity.getEmail());
        return AuthResponse.builder()
                .jwt(jwt)
                .email(userEntity.getEmail())
                .build();
    }

    @Transactional
    public void confirmUser(String email, String confirmationToken) {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email address."));

        if (!confirmationToken.equals(userEntity.getConfirmationToken())) {
            throw new IllegalArgumentException("Invalid confirmation token.");
        }

        userEntity.setEnabled(true);
        userEntity.setConfirmationToken(null);

        // TODO: Оновлення ролі користувача
        userRepository.save(userEntity);
    }

    @Transactional
    public void updatePassword(String jwt, PasswordUpdateRequest passwordUpdateRequest) {
        log.info("jwt: {}", jwt);
        DecodedJWT decodedJWT = jwtVerifier.verify(jwt);
        log.info("decodedJWT: {}", decodedJWT);
        String emailFromToken = decodedJWT.getSubject();
        log.info("emailFromToken: {}", emailFromToken);
        if(!emailFromToken.equals(passwordUpdateRequest.email())) {
            throw new UserNotFoundException("The token does not belong to this userEntity");
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsServices.loadUserByUsername(emailFromToken);
        UserEntity userEntity = userDetails.getUserEntity();

        String encodeNewPassword = passwordEncoder.encode(passwordUpdateRequest.newPassword());
        userEntity.setPassword(encodeNewPassword);
        userRepository.save(userEntity);
    }
    @Transactional
    public void recoveryPassword(String jwt, PasswordRecoveryRequest passwordRecoveryRequest) {
        DecodedJWT decodedJWT = jwtVerifier.verify(jwt);
        String emailFromToken = decodedJWT.getSubject();
        if (!emailFromToken.equals(passwordRecoveryRequest.email())) {
            throw new UserNotFoundException("The token does not belong to this userEntity");
        }

        // Генеруємо новий тимчасовий пароль
        String temporaryPassword = generateTemporaryPassword();
        String encodedPassword = passwordEncoder.encode(temporaryPassword);

        // Оновлюємо пароль у базі даних
        UserEntity userEntity = userRepository.findByEmail(emailFromToken)
                .orElseThrow(() -> new UserNotFoundException("UserEntity not found with email: " + emailFromToken));
        userEntity.setPassword(encodedPassword);
        userRepository.save(userEntity);

        // Відправляємо тимчасовий пароль на email користувача
        emailSender.sendSimpleMessage(passwordRecoveryRequest.email(), "Відновлення паролю",
                "Ваш тимчасовий пароль: " + temporaryPassword);

    }
    private String generateTemporaryPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

}



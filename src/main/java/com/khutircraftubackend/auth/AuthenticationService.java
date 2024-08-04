package com.khutircraftubackend.auth;

import com.khutircraftubackend.auth.request.LoginRequest;
import com.khutircraftubackend.auth.request.RegisterRequest;
import com.khutircraftubackend.auth.response.AuthResponse;
import com.khutircraftubackend.jwtToken.JwtUtils;
import com.khutircraftubackend.mail.MailService;
import jakarta.persistence.PostUpdate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
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
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;


    /**
     * Аутентифікує користувача за вказаними email та паролем.
     *
     * @param request Запит на авторизацію, що містить email та пароль.
     * @return Об'єкт AuthResponse з JWT токеном та email користувача.
     * @throws BadCredentialsException Якщо вказані невірні дані для входу.
     * @throws AuthenticationException Якщо виникла непередбачена помилка під час аутентифікації.
     */
    public AuthResponse authenticate(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password())
            );
            // Fetch user by email
            User user = userRepository.findByEmail(request.email())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Користувач з таким email не знайдений"));

            // Генерація JWT токена
            String jwt = jwtUtils.generateJwtToken(user.getEmail());

            // Build and return authentication response
            return AuthResponse.builder()
                    .jwt(jwt)
                    .email(user.getEmail())
                    .build();
        } catch (BadCredentialsException e) {
            log.error("Невірні дані для входу для email: {}", request.email());
            throw new BadCredentialsException("Невірний email або пароль", e);
        } catch (AuthenticationException e) {
            log.error("Помилка під час аутентифікації для email: {}", request.email(), e);
            throw new RuntimeException("Виникла помилка під час аутентифікації", e);
        }
    }

    @Transactional
    @PostUpdate
    public void registerNewUser(RegisterRequest request) {
        if(userRepository.existsByEmail(request.email())) {
    throw new IllegalArgumentException("Цей email зайнятий, використайте інший або ввійдіть під цим");
    }
        String token = jwtUtils.generateJwtToken(request.email());

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .confirmationToken(UUID.randomUUID().toString())
                .enabled(false)
                .role(Role.GUEST)
                .jwt(token)
                .build();

        userRepository.saveAndFlush(user);

        //String confirmationUrl = "http://localhost:8080/v1/user/confirm?token=" + user.getConfirmationToken();
        //log.info("Sending confirmation email to: {}", user.getEmail()); // Логування електронної адреси
        //mailService.sendEmail(user.getEmail(), "Please confirm your registration by clicking on the following link: " + confirmationUrl);
    }

    @Transactional
    public void confirmUser(String email, String confirmationToken) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email address."));

        if (!confirmationToken.equals(user.getConfirmationToken())) {
            throw new IllegalArgumentException("Invalid confirmation token.");
        }

        user.setEnabled(true);
        user.setConfirmationToken(null);

        // Створення Authentication об'єкта
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                user.getPassword(),
                Collections.emptyList() // Додати ролі або authorities, якщо потрібно
        );

        // Генерація JWT токена
        String jwt = jwtUtils.generateJwtToken(email);

        user.setJwt(jwt);

        // TODO: Оновлення ролі користувача
        userRepository.save(user);
    }

    @Transactional
    public void updateJwtToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        String newToken = jwtUtils.generateJwtToken(email);
        user.setJwt(newToken);
        userRepository.saveAndFlush(user);
    }

//    @Transactional
//    public boolean updatePassword(PasswordUpdateRequest passwordUpdateRequest) {
//        Optional<User> userOptional = userRepository.findByEmail(passwordUpdateRequest.email());
//        if (userOptional.isPresent()) {
//            User user = userOptional.get();
//            if (passwordEncoder.matches(passwordUpdateRequest.oldPassword(), user.getPassword())) {
//                user.setPassword(passwordEncoder.encode(passwordUpdateRequest.newPassword()));
//                userRepository.save(user);
//                return true;
//            }
//            }
//            return false;
//        }


//    public void initiatePasswordRecovery(String email) throws UserNotFoundException {
//        Optional<User> userOptional = userRepository.findByEmail(email);
//        if (userOptional.isPresent()) {
//            User user = userOptional.get();
//            // Генерація токена для відновлення пароля та відправка електронного листа
//            String token = jwtUtils.generateJwtToken(user.getEmail());
//            sendPasswordRecoveryEmail(email, token);
//        }
//    }

//    private void sendPasswordRecoveryEmail(String email, String token) {
//        String recoveryUrl = "http://your-app-url.com/reset-password?token=" + token;
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(email);
//        message.setSubject("Password Recovery");
//        message.setText("To reset your password, click the link below:\n" + recoveryUrl);
//        emailService.sendEmail(email, recoveryUrl);
//        log.info("Password recovery email sent to: {}", email);
//    }


//    @Transactional
//    public void resetPassword(String token, String newPassword) throws InvalidTokenException {
//        String email = jwtUtils.getJwtEmailFromToken(token);
//        Optional<User> userOptional = userRepository.findByEmail(email);
//        if (userOptional.isPresent() && jwtUtils.validateJwtToken(token)) {
//            User user = userOptional.get();
//            user.setPassword(passwordEncoder.encode(newPassword));
//            userRepository.save(user);
//        } else {
//            throw new InvalidTokenException("Invalid or expired token");
//        }
//    }
}


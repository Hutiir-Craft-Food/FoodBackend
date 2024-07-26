package com.khutircraftubackend.services;

import com.khutircraftubackend.dto.UserDTO;
import com.khutircraftubackend.dto.security.PasswordUpdateRequest;
import com.khutircraftubackend.mapper.UserMapper;
import com.khutircraftubackend.models.Role;
import com.khutircraftubackend.models.User;
import com.khutircraftubackend.repositories.UserRepository;
import com.khutircraftubackend.security.JwtUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Клас UserService реалізує бізнес-логіку для роботи з користувачами.
 * <p>
 * Цей клас містить методи для створення, оновлення, видалення та отримання користувачів.
 * </p>
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final JavaMailSender mailSender;
    private final SendinblueEmailService sendinblueEmailService;

    /**
     * Реєструє нового користувача.
     * <p>
     * Метод перетворює UserDTO в User, хешує пароль, генерує JWT токен та код підтвердження.
     * Потім зберігає користувача в базі даних і надсилає електронного листа з кодом підтвердження.
     * </p>
     *
     * @param userDTO дані для реєстрації користувача
     * @return UserDTO об'єкт зареєстрованого користувача
     */

    @Transactional
        public UserDTO registerNewUser(UserDTO userDTO) {
            User user = UserMapper.INSTANCE.userDTOToUser(userDTO);

            if(userDTO.isPasswordMatching()) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                switch (userDTO.role()) {
                    case SELLER -> user.setRole(Role.SELLER);
                    case BUYER -> user.setRole(Role.BUYER);
                    case ADMIN -> user.setRole(Role.ADMIN);
                    default -> user.setRole(Role.GUEST);
                }

                user.setEnabled(false);//до підтвердження електронною поштою

                // Генерація та збереження JWT
                String jwt = jwtUtils.generateJwtToken(user.getEmail());
                user.setJwt(jwt);
                log.info("jwt: {}", jwt);

                // Генерація та збереження коду підтвердження
                String confirmationCode = generateConfirmationCode();
                user.setConfirmationCode(confirmationCode);
                log.info("code: {}", confirmationCode);
            }
            User savedUser = userRepository.save(user);
            sendEmail(userDTO.email(), "Confirm your email", "To confirm your email," +
                    " click the link below:\n http://your-app-url.com/confirm?code=" +
                    userDTO.confirmationCode());// Відправка коду підтвердження
            return UserMapper.INSTANCE.userToUserDTO(savedUser);
    }

    /**
     * Відправляє електронний лист.
     *
     * @param email адреса електронної пошти одержувача
     * @param subject тема листа
     * @param text текст листа
     */
    private void sendEmail(String email, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(text);
        //mailSender.send(message);
        sendinblueEmailService.sendEmail(email, subject, text);
        log.info("{} email sent to: {}", subject, email);
    }

    /**
     * Генерує випадковий код підтвердження.
     *
     * @return випадковий код підтвердження
     */
    private String generateConfirmationCode() {
        //Генерація випадкового коду підтвердження
        return UUID.randomUUID().toString();
    }

    /**
     * Активує користувача за кодом підтвердження.
     * <p>
     * Метод знаходить користувача за кодом підтвердження та активує його.
     * </p>
     *
     * @param confirmationCode код підтвердження
     */
    @Transactional
    public void enableUser(String confirmationCode) {
        User user = userRepository.findByConfirmationCode(confirmationCode);
        if(user != null) {
            user.setEnabled(true);
            userRepository.save(user);
        }
    }

    /**
     * Знаходить користувача за електронною поштою.
     *
     * @param email адреса електронної пошти користувача
     * @return Optional<UserDTO> об'єкт UserDTO, якщо користувач знайдений, інакше - пустий Optional
     */
    public Optional<UserDTO> findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserMapper.INSTANCE::userToUserDTO);
    }

    /**
     * Оновлює пароль користувача.
     *
     * @param passwordUpdateRequest запит на оновлення пароля, що містить старий та новий паролі
     * @return true, якщо пароль успішно оновлено, інакше - false
     */
    @Transactional
    public boolean updatePassword(PasswordUpdateRequest passwordUpdateRequest) {
        Optional<UserDTO> userDTOOptional = findUserByEmail(passwordUpdateRequest.getEmail());
        if(userDTOOptional.isPresent()) {
            User user = UserMapper.INSTANCE.userDTOToUser(userDTOOptional.get());
            if(passwordEncoder.matches(passwordUpdateRequest.getOldPassword(), user.getPassword())) {
                user.setPassword(passwordEncoder.encode(passwordUpdateRequest.getNewPassword()));
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    /**
     * Ініціює відновлення пароля для користувача.
     * <p>
     * Метод генерує токен для відновлення пароля і відправляє електронний лист з посиланням для відновлення пароля.
     * </p>
     *
     * @param email адреса електронної пошти користувача, для якого потрібно відновити пароль
     */
    public void initiatePasswordRecovery(String email) {
        Optional<UserDTO> userDTOOptional = findUserByEmail(email);
        if (userDTOOptional.isPresent()) {
            // Генерація токена для відновлення пароля та відправка електронного листа
            String token = jwtUtils.generateJwtToken(email);
            sendEmail(email, "Password Recovery", "To reset your password, " +
                    "click the link below:\n http://your-app-url.com/reset-password?token=" +  token);
        }
    }
}

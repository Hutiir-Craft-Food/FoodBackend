package com.khutircraftubackend.services;

import com.khutircraftubackend.dto.security.PasswordUpdateRequest;
import com.khutircraftubackend.dto.UserDTO;
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

    /**
     * Клас UserService реалізує бізнес-логіку для роботи з користувачами.
     */

    @Transactional
    public UserDTO registerNewUser(UserDTO userDTO) {
        User user = UserMapper.INSTANCE.userDTOToUser(userDTO);

        if(userDTO.isPasswordMatching()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole(userDTO.role() == Role.SELLER ? Role.SELLER : Role.BUYER);
            user.setEnabled(false);//до підтвердження електронною поштою
            user.setJwt(jwtUtils.generateJwtToken(user.getEmail()));
        }
            User savedUser = userRepository.save(user);// надіслати сюди логіку електронного листа з підтвердженням
            return UserMapper.INSTANCE.userToUserDTO(savedUser);
    }

    public Optional<UserDTO> findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserMapper.INSTANCE::userToUserDTO);
    }
    @Transactional
    public void enableUser(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isPresent()) {
            User user = userOptional.get();
            user.setEnabled(true);
            userRepository.save(user);
        }
    }

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

    public void initiatePasswordRecovery(String email) {
        Optional<UserDTO> userDTOOptional = findUserByEmail(email);
        if(userDTOOptional.isPresent()) {
            // логіка відправки листа для відновлення пароля
            UserDTO userDTO = userDTOOptional.get();
            // Генерація токена для відновлення пароля та відправка електронного листа
            User user = UserMapper.INSTANCE.userDTOToUser(userDTOOptional.get());
            String token = generatePasswordRecoveryToken(user.getEmail());
            sendPasswordRecoveryEmail(user.getEmail(), token);
        }
    }
    private String generatePasswordRecoveryToken(String email) {
        return jwtUtils.generateJwtToken(email);
    }

    private void sendPasswordRecoveryEmail(String email, String token) {
        String recoveryUrl = "http://your-app-url.com/reset-password?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Recovery");
        message.setText("To reset your password, click the link below:\n" + recoveryUrl);
        mailSender.send(message);
        log.info("Password recovery email sent to: {}", email);
    }
}

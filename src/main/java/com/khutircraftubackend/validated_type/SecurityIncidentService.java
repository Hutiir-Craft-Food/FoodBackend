package com.khutircraftubackend.validated_type;

import com.khutircraftubackend.mail.EmailSender;
import com.khutircraftubackend.product.ProductEntity;
import com.khutircraftubackend.user.Role;
import com.khutircraftubackend.user.UserEntity;
import com.khutircraftubackend.user.UserRepository;
import com.khutircraftubackend.user.UserService;
import com.khutircraftubackend.validated_type.exception.SuspiciousFileException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SecurityIncidentService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final EmailSender emailSender;

    public void handleSuspiciousFile(ProductEntity product, SuspiciousFileException exception) {
        UserEntity user = userService.findUserByProduct(product);
        userService.updateUserEnabledStatus(user, false);

        List<String> adminEmails = userRepository.findAllEmailsByRole(Role.ADMIN);
        String subject = "Підозрілий файл завантажено користувачем";
        String body = buildIncidentMessage(user.getEmail(), exception);

        adminEmails.forEach(email -> emailSender.sendSimpleMessage(email, subject, body));
    }

    private String buildIncidentMessage(String userEmail, SuspiciousFileException exception) {
        String threatLevel = ThreatLevelResolver.resolve(exception.getDetectedType());
        return String.format("""
                Виявлено навмисну маскировку файлу:
                Користувач: %s
                Файл: %s
                Заявлений тип: %s
                Реальний тип: %s
                Користувач тимчасово заблокований.
                
                Рівень загрози: %s
                """,
                userEmail, exception.getFileName(),
                exception.getDeclaredType(),
                exception.getDetectedType(),
                threatLevel
        );
    }
}

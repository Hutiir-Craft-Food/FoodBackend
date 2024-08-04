package com.khutircraftubackend.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service for sending emails using Sendinblue API.
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {
private final JavaMailSender javaMailSender;

//     @Value("${MAIL_HOST}")
//     private String emailFrom;

    public void sendEmail(String to, String confirmationUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("7954fa001@smtp-brevo.com");
        message.setTo(to);
        message.setSubject("Email Confirmation");
        message.setText(confirmationUrl);
        try {
            javaMailSender.send(message);
            log.info("Email sent successfully to {}", to);
        } catch (MailException e) {
            log.error("Error sending email to {}: {}", to, e.getMessage());
        }
    }
}

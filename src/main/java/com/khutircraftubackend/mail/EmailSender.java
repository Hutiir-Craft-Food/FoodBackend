package com.khutircraftubackend.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
        * Service for sending email messages.
        * This service uses JavaMailSender to send emails asynchronously.
        *
        * Configuration is set up for virtual testing using Mailtrap.
        * For more details, visit: https://mailtrap.io/inboxes/3062049/messages
        */

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailSender {
    private final JavaMailSender mailSender;

    /**
     * Sends a simple email message.
     * This method is asynchronous to avoid blocking the main thread.
     *
     * @param to the recipient email address
     * @param subject the subject of the email
     * @param text the body of the email
     */
        @Async
        public void sendSimpleMessage(String to, String subject, String text) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        }
    }

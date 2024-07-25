package com.khutircraftubackend.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class SendinblueEmailService {

    @Value("${sendinblue.api-key}")
    private String apiKey;

    public void sendEmail(String to, String subject, String content) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.sendinblue.com/v3/smtp/email";

        HttpHeaders headers = new HttpHeaders();
        headers.set("api-key", apiKey);
        headers.set("Content-Type", "application/json");

        Map<String, Object> emailData = new HashMap<>();
        emailData.put("sender", Map.of("name", "Your Name", "email", "your-email@example.com"));
        emailData.put("to", new Map[]{Map.of("email", to)});
        emailData.put("subject", subject);
        emailData.put("htmlContent", content);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(emailData, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        System.out.println(response.getStatusCode());
        System.out.println(response.getBody());
    }
}

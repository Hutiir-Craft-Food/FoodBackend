package com.khutircraftubackend.ratelimit;

import com.khutircraftubackend.exception.httpStatus.ForbiddenException;
import com.khutircraftubackend.exception.httpStatus.ToManyRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
@Slf4j
public class SecurityService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final int MAX_LOGIN_ATTEMPTS = 3;
    private static final int BLOCK_DURATION_MINUTES = 30;
    private static final int IP_REQUEST_LIMIT = 5;
    private static final int IP_WINDOW_SECONDS = 60;
    private static final List<String> ALLOWED_USER_AGENTS = List.of(
            "Mozilla",                  // Chrome, Firefox, Safari, Edge и т.д.
            "PostmanRuntime",           // Postman
            "vscode-restclient",        // REST Client extension в VS Code
            "curl",                     // curl
            "Wget",                     // wget
            "Java-http-client"          // HttpClient Java 11+
    );

    public void checkBruteforceAttempt(String email) {
        String key = "bruteforce:" + email;
        Long attempts = redisTemplate.opsForValue().increment(key);

        if (attempts == null) {
            log.warn("Failed to increment brute-force counter for key {}", key);
            return;
        }

        if (attempts == 1) {
            redisTemplate.expire(key, BLOCK_DURATION_MINUTES, TimeUnit.MINUTES);
        }

        if (attempts > MAX_LOGIN_ATTEMPTS) {
            throw new ToManyRequestException("Ваш акаун тимчасово заблоковано. Повторіть спробу пізніше.");
        }
    }

    public void checkDdosProtection(String ip) {
        String key = "ddos:" + ip;
        Long count = redisTemplate.opsForValue().increment(key);

        if (count == null) {
            log.warn("Failed to increment ddos counter for key {}", key);
            return;
        }

        if (count == 1) {
            redisTemplate.expire(key, IP_WINDOW_SECONDS, TimeUnit.SECONDS);
        }

        if (count > IP_REQUEST_LIMIT) {
            throw new ToManyRequestException("Забагато запитів з вашої ІР-адреси.");
        }
    }

    public void resetBruteforceAttempts(String email) {
        String key = "bruteforce:" + email;
        redisTemplate.delete(key);
    }

    public void validateUserAgentIsAllowed(String userAgent) {
        if (userAgent == null || ALLOWED_USER_AGENTS.stream().noneMatch(userAgent::contains)) {
            throw new ForbiddenException("Доступ забороннений");
        }
    }
}

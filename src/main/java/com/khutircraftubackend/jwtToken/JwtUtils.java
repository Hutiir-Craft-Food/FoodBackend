package com.khutircraftubackend.jwtToken;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Клас JwtUtils містить методи для створення та перевірки JWT токенів.
 */
@Slf4j
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.expiration}")
    private int jwtExpirationSec;

    private Algorithm algorithm;
    private JWTVerifier jwtVerifier;

    @PostConstruct
    public void init() {
        algorithm = Algorithm.HMAC512(jwtSecret);
        jwtVerifier = JWT.require(algorithm).build();
    }

    public String generateJwtToken(String email) {
        return JWT.create()
                .withSubject(email)
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpirationSec * 1000L))
                .sign(algorithm);
    }

    public String getJwtEmailFromToken(String token) {
            log.info("Token to verify: {}", token);
        DecodedJWT jwt = jwtVerifier.verify(token);
        return jwt.getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            log.info("Validating token: {}", authToken);
            DecodedJWT jwt = jwtVerifier.verify(authToken);
            log.info("Token is valid: {}", jwt.getSubject());
            return true;
        } catch (JWTVerificationException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        }
        return false;
    }

    public boolean isTokenExpired(String token) {
        try {
            DecodedJWT jwt = jwtVerifier.verify(token);
            return jwt.getExpiresAt().before(new Date());
        } catch (JWTVerificationException e) {
            log.error("Error checking token verification: {}", e.getMessage());
        }
        return false;
    }
}


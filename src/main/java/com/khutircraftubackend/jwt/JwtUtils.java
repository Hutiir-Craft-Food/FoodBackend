package com.khutircraftubackend.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Клас JwtUtils містить методи для створення та перевірки JWT токенів.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtils {
	
	private final Algorithm algorithm;
	@Value("${jwt.expiration}")
	private long jwtExpirationMillis;
	
	public String generateJwtToken(String email) {
		return JWT.create()
				.withSubject(email)
				.withIssuedAt(new Date(System.currentTimeMillis()))
				.withExpiresAt(new Date(System.currentTimeMillis() + jwtExpirationMillis))
				.sign(algorithm);
	}
}


package com.khutircraftubackend.security;

import com.khutircraftubackend.security.exceptionJwt.*;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.util.Date;

/**
 * Клас JwtUtils містить методи для створення та перевірки JWT токенів.
 */

@RequiredArgsConstructor
@Slf4j
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationSec;

    public String generateJwtToken(String email) {
        String jwt = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(jwtExpirationSec)))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
        log.info("Generate JWT: {}", jwt);
        return jwt;
    }

    public String getJwtUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Validates the given JWT token.
     *
     * @param token the JWT token to validate
     * @return true if the token is valid, false otherwise
     * @throws InvalidJwtSignatureException if the JWT signature is invalid
     * @throws MalformedJwtTokenException if the JWT token format is invalid
     * @throws ExpiredJwtTokenException if the JWT token is expired
     * @throws UnsupportedJwtTokenException if the JWT token is unsupported
     * @throws EmptyJwtClaimsException if the JWT claims string is empty
     */

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}. Token: {}", e.getMessage(), token);
            throw new InvalidJwtSignatureException("Invalid JWT signature");
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token format: {}. Token: {}", e.getMessage(), token);
            throw new MalformedJwtTokenException("Invalid JWT token format");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token: {}. Token: {}", e.getMessage(), token);
            throw new ExpiredJwtTokenException("Expired JWT token");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token: {}. Token: {}", e.getMessage(), token);
            throw new UnsupportedJwtTokenException("Unsupported JWT token");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}. Token: {}", e.getMessage(), token);
            throw new EmptyJwtClaimsException("JWT claims string is empty");
        }
    }
}

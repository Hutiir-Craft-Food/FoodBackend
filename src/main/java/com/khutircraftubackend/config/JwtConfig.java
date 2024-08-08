package com.khutircraftubackend.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Bean
    public Algorithm algorithm(@Value("${jwt.secret}") String jwtSecret) {
        return Algorithm.HMAC512(jwtSecret);
    }

    @Bean
    public JWTVerifier jwtVerifier(@Autowired Algorithm algorithm) {
        return JWT.require(algorithm).build();
    }

}

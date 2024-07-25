package com.khutircraftubackend.Security;

import com.khutircraftubackend.security.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class JwtUtilsTest {

    @Autowired
    public JwtUtils jwtUtils;

    @Test
    public void testGenerateJwtToken() {
        String email = "test@example.com";
        String jwt = jwtUtils.generateJwtToken(email);
        System.out.println("Generated JWT: " + jwt);
    }

    @Test
    public void testValidateJwtToken() {
        String email = "test@example.com";
        String jwt = jwtUtils.generateJwtToken(email);

        boolean isValid = jwtUtils.validateJwtToken(jwt);
        System.out.println("Is JWT valid? " + isValid);
    }
}
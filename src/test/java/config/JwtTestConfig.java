package config;

import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@Profile("test")
@TestConfiguration
public class JwtTestConfig {
    
    @Bean
    public Algorithm algorithm() {
        
        return Algorithm.HMAC512("dummy-secret");
    }
}

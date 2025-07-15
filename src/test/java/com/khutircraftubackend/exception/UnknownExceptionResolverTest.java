package com.khutircraftubackend.exception;

import com.khutircraftubackend.auth.request.LoginRequest;
import com.khutircraftubackend.config.JwtTestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@AutoConfigureMockMvc
@ActiveProfiles({"local","test"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"spring.main.allow-bean-definition-overriding=true"})
@DirtiesContext
@Import({JwtTestConfig.class, OverridingTestConfig.class})
class UnknownExceptionResolverTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Test
    @DisplayName("Returns valid response in case of internal server error")
    void test_valid_response_with_unknown_internal_exception() {
        String fakeEmail = "fake@email.com";
        String fakePassword = "fakePassword123!";

        GlobalErrorResponse errorResponse = restTemplate.postForObject(
                "http://localhost:{port}/v1/auth/login",
                new LoginRequest(fakeEmail, fakePassword),
                GlobalErrorResponse.class, port);

        assertNotNull(errorResponse.timestamp());
        assertEquals(500, errorResponse.status());
        assertEquals("Тимчасова помилка сервера, зверніться до адміністрації сайту", errorResponse.message());
        assertEquals("/v1/auth/login", errorResponse.path());
    }
}
/**
 * additional information:
 * https://blog.sammkinng.in/blogs/checked-exception-is-invalid-for-this-method
 * https://docs.spring.io/spring-boot/reference/testing/spring-boot-applications.html
 */
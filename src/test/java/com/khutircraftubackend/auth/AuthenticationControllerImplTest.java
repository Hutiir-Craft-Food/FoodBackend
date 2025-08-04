package com.khutircraftubackend.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.khutircraftubackend.auth.request.LoginRequest;
import com.khutircraftubackend.config.JwtTestConfig;
import com.khutircraftubackend.exception.GlobalErrorResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles({"local", "test"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"spring.main.allow-bean-definition-overriding=true"})
@Import(JwtTestConfig.class)
class AuthenticationControllerImplTest {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Algorithm algorithm;
    
    @Nested
    class WrongTokenTests {
        
//        @Test
//        @DisplayName("should fail on expired token")
//        void fail_when_authenticating_with_expired_token() {
//
//            String email = "seller@example.com";
//
//            String expiredToken = JWT.create()
//                    .withSubject(email)
//                    .withIssuedAt(new Date(System.currentTimeMillis() - 1000))
//                    .withExpiresAt(new Date(System.currentTimeMillis() - 100))
//                    .sign(algorithm);
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.add("Authorization", "Bearer " + expiredToken);
//            HttpEntity<String> request = new HttpEntity<String>(headers);
//
//            int offset = 0, limit = 4;
//
//            GlobalErrorResponse errorResponse = restTemplate.exchange(
//                    "http://localhost:{port}/v1/products?offset={offset}&limit={limit}",
//                    HttpMethod.GET, request, GlobalErrorResponse.class,
//                    port, offset, limit).getBody();
//
//            assertNotNull(errorResponse, "Expected an error response but got null");
//            assertEquals(401, errorResponse.status());
//            assertTrue(errorResponse.message().startsWith("Недійсний токен автентифікації"));
//            assertEquals("/v1/products", errorResponse.path());
//        }
//
//        @Test
//        @DisplayName("should fail when someone forges token's signature")
//        void fail_when_authenticating_with_token_having_wrong_signature() {
//
//            String email = "seller@example.com";
//
//            Algorithm wrongAlgorithm = Algorithm.HMAC512("secret");
//
//            String forgedToken = JWT.create()
//                    .withSubject(email)
//                    .withIssuedAt(new Date(System.currentTimeMillis()))
//                    .withExpiresAt(new Date(System.currentTimeMillis() + 1000))
//                    .sign(wrongAlgorithm);
//
//            System.out.println("ForgedToken: " + forgedToken);
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.add("Authorization", "Bearer " + forgedToken);
//            HttpEntity<String> request = new HttpEntity<String>(headers);
//
//            int offset = 0, limit = 4;
//
//            GlobalErrorResponse errorResponse = restTemplate.exchange(
//                    "http://localhost:{port}/v1/products?offset={offset}&limit={limit}",
//                    HttpMethod.GET, request, GlobalErrorResponse.class,
//                    port, offset, limit).getBody();
//
//            assertNotNull(errorResponse, "Expected an error response but got null");
//            assertEquals(401, errorResponse.status());
//            assertTrue(errorResponse.message().startsWith("Недійсний токен автентифікації"));
//            assertEquals("/v1/products", errorResponse.path());
//        }
//
//        @Test
//        @SneakyThrows
//        void fail_and_return_formatted_response_on_wrong_crud_method() {
//            MockHttpServletRequestBuilder requestBuilder = get("/v1/auth/login")
//                    .contentType(MediaType.APPLICATION_JSON);
//
//            String responseBody = mockMvc.perform(requestBuilder)
//                    //.andDo(print()) // this is optional
//                    .andExpect(status().is4xxClientError())
//                    .andReturn().getResponse().getContentAsString();
//
//            GlobalErrorResponse errorResponse = objectMapper.readValue(responseBody, GlobalErrorResponse.class);
//
//            assertNotNull(errorResponse.timestamp());
//            assertEquals(405, errorResponse.status());
//            assertEquals("Request method 'GET' is not supported", errorResponse.message());
//            assertEquals("/v1/auth/login", errorResponse.path());
//        }
//    }
//
//    @Nested
//    class ValidationTests {
//
//        @Test
//        @SneakyThrows
//        void test_credentials_validation() {
//            LoginRequest loginRequest = new LoginRequest("wrong-email", "pa$$word");
//
//            MockHttpServletRequestBuilder requestBuilder = post("/v1/auth/login")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(objectMapper.writeValueAsString(loginRequest));
//
//            String responseBody = mockMvc.perform(requestBuilder)
//                    //.andDo(print()) // this is optional
//                    .andExpect(status().is4xxClientError())
//                    .andReturn().getResponse().getContentAsString();
//
//            GlobalErrorResponse errorResponse = objectMapper.readValue(responseBody, GlobalErrorResponse.class);
//
//            assertNotNull(errorResponse.data());
//            assertEquals(1, ((Map<?, ?>) errorResponse.data()).size());
//            assertNotNull(((Map<?, ?>) errorResponse.data()).get("email"));
//        }
    }
}
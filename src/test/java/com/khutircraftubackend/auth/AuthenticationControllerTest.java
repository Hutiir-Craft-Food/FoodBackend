package com.khutircraftubackend.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khutircraftubackend.auth.request.LoginRequest;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles({"local", "test"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Nested
    class WrongTokenTests {

        @Test
        @DisplayName("should fail on expired token")
        void fail_when_authenticating_with_expired_token () {

            // TODO:
            //  programmatically generate expired token here:
            String expiredToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9." +
                    "eyJzdWIiOiJzZWxsZXJAZXhhbXBsZS5jb20iLCJleHAiOjE3Mzk4NzI2NzUsImlhdCI6MTczOTg3MjU4NX0." +
                    "01eFWh1MTQ4rc24uaY0A1G97SlwTtmm-qGWrwwo6fc6h6eRXIJQLhUwfVlegXeCN0U_1G6exe9UFvIRzAHqDLQ";

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + expiredToken);
            HttpEntity<String> request = new HttpEntity<String>(headers);

            int offset = 0, limit = 4;

            GlobalErrorResponse errorResponse = restTemplate.exchange(
                    "http://localhost:{port}/v1/products?offset={offset}&limit={limit}",
                    HttpMethod.GET, request, GlobalErrorResponse.class,
                    port, offset, limit).getBody();

            assertEquals(401, errorResponse.status());
            assertTrue(errorResponse.message().startsWith("The Token has expired"));
            assertEquals("/v1/products", errorResponse.path());
        }

        @Test
        @DisplayName("should fail when someone forges token's signature")
        void fail_when_authenticating_with_token_having_wrong_signature () {
            // TODO:
            //  programmatically generate token with valid claims but wrong signature:
            String forgedToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9." +
                    "eyJzdWIiOiJibGFoLXVzZXIiLCJleHAiOjE3NDAzNTM2MjIsImlhdCI6MTc0MDM1MzU2Mn0." +
                    "gXCdsHdrNsK80dIwYAzIiC78uIteujERUTdJWHNTRnMhh_-rKn7BlTn3x1fxwXVhHah2Sc5zySJwBx6ADmW_Ww";

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + forgedToken);
            HttpEntity<String> request = new HttpEntity<String>(headers);

            int offset = 0, limit = 4;

            GlobalErrorResponse errorResponse = restTemplate.exchange(
                    "http://localhost:{port}/v1/products?offset={offset}&limit={limit}",
                    HttpMethod.GET, request, GlobalErrorResponse.class,
                    port, offset, limit).getBody();

            assertEquals(401, errorResponse.status());
            assertTrue(errorResponse.message().startsWith("The Token's Signature resulted invalid"));
            assertEquals("/v1/products", errorResponse.path());
        }

        @Test
        @SneakyThrows
        public void fail_and_return_formatted_response_on_wrong_crud_method () {
            MockHttpServletRequestBuilder requestBuilder = get("/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON);

            String responseBody = mockMvc.perform(requestBuilder)
                    //.andDo(print()) // this is optional
                    .andExpect(status().is4xxClientError())
                    .andReturn().getResponse().getContentAsString();

            GlobalErrorResponse errorResponse = objectMapper.readValue(responseBody, GlobalErrorResponse.class);

            assertNotNull(errorResponse.timestamp());
            assertEquals(405, errorResponse.status());
            assertEquals("Request method 'GET' is not supported", errorResponse.message());
            assertEquals("/v1/auth/login", errorResponse.path());
        }
    }

    @Nested
    class ValidationTests {

        @Test
        @SneakyThrows
        public void test_credentials_validation () {
            LoginRequest loginRequest = new LoginRequest("wrong-email", "pa$$word");

            MockHttpServletRequestBuilder requestBuilder = post("/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest));

            String responseBody = mockMvc.perform(requestBuilder)
                    //.andDo(print()) // this is optional
                    .andExpect(status().is4xxClientError())
                    .andReturn().getResponse().getContentAsString();

            GlobalErrorResponse errorResponse = objectMapper.readValue(responseBody, GlobalErrorResponse.class);

            assertNotNull(errorResponse.data());
            assertEquals(2, ((Map<?, ?>) errorResponse.data()).size());
            assertNotNull(((Map<?, ?>) errorResponse.data()).get("email"));
            assertNotNull(((Map<?, ?>) errorResponse.data()).get("password"));
        }
    }
}
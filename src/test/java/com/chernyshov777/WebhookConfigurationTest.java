package com.chernyshov777;

import com.chernyshov777.domain.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"management.port=0"})
public class WebhookConfigurationTest {

    private static final Logger logger = LoggerFactory.getLogger(WebhookConfigurationTest.class);

    @LocalServerPort
    private int port;

    @Value("${management.port}")
    private int managementPort;

    @Autowired
    private TestRestTemplate restTemplate;

    private Payment payment;

    @Before
    public void init() {
        payment = new Payment();
        payment.setIntent("test");
        payment.setNotificationUrl("http://localhost:8080/msg/receive");
        payment.setPayer(new Payer("test@example.com"));
        payment.setTransaction(new Transaction(1L, new Amount(1000, "USD"), "test"));
    }

    @Test
    public void shouldReturn200WhenSendingRequestToController() throws Exception {
        logger.debug("shouldReturn200WhenSendingRequestToController");

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<Payment> request = new HttpEntity<>(payment ,headers);

        ResponseEntity<PaymentResponse> entity = restTemplate.postForEntity(
                "http://localhost:" + port + "/payments/payment",
                request,
                PaymentResponse.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void shouldReturn200WhenSendingRequestToManagementEndpoint() throws Exception {
        logger.debug("shouldReturn200WhenSendingRequestToManagementEndpoint");

        ResponseEntity<PaymentResponse> entity = restTemplate.postForEntity(
                "http://localhost:" + managementPort + "/payments/payment",
                payment,
                PaymentResponse.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }


    @Test
    public void tokenTest() {
        logger.debug("shouldReturnAccessToken");

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.add("Accept-Language", "en_US");
        headers.add("Authorization", "Basic dXNlcjpzZWNyZXQ=");
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity<TokenRequestBody> request = new HttpEntity<>(new TokenRequestBody("client_credentials"), headers);
        ResponseEntity<TokenRequestBody> tokenRequestBodyResponseEntity =
                restTemplate.postForEntity("http://localhost:" + port + "/oauth2/token",
                        request,
                        TokenRequestBody.class);
        assertThat(tokenRequestBodyResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private class TokenRequestBody {
        @JsonProperty("grant_type")
        private String grantType;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private class TokenResponseBody {
        private String scope;
        @JsonProperty("Access-Token")
        private String accessToken;
        @JsonProperty("token_type")
        private String tokenType;
        @JsonProperty("expires_in")
        private long expiresIn;
    }
}
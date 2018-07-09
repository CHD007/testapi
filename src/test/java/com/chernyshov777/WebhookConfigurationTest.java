package com.chernyshov777;

import com.chernyshov777.domain.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
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
@EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class})
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
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
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

}
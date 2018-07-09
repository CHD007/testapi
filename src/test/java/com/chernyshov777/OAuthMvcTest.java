package com.chernyshov777;

import com.chernyshov777.domain.Amount;
import com.chernyshov777.domain.Payer;
import com.chernyshov777.domain.Payment;
import com.chernyshov777.domain.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.JacksonJsonParser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = Application.class)
@ActiveProfiles("mvc")
public class OAuthMvcTest {
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    private Payment payment;

    @Autowired
    ObjectMapper objectMapper;

    @Before
    public void init() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
                .apply(springSecurity())
                .build();

        payment = new Payment();
        payment.setIntent("test");
        payment.setNotificationUrl("http://localhost:8080/msg/receive");
        payment.setPayer(new Payer("test@example.com"));
        payment.setTransaction(new Transaction(1L, new Amount(1000, "USD"), "test"));
    }

    protected String obtainAccessToken() throws Exception {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "client_credentials");
        params.add("client_id", "user");
        params.add("secret", "secret");

        ResultActions result
                = mockMvc.perform(post("/oauth2/token")
                .params(params)
                .with(httpBasic("user","secret"))
                .accept("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));

        String resultString = result.andReturn().getResponse().getContentAsString();

        JacksonJsonParser jsonParser = new JacksonJsonParser();
        return jsonParser.parseMap(resultString).get("access_token").toString();
    }

    @Test
    public void givenNoToken_whenGetSecureRequest_thenUnauthorized() throws Exception {
        mockMvc.perform(get("/payments/payment")
                .param("email", "test@example.com"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void givenInvalidAccessToken_whenGetSecureRequest_thenUnauthorized() throws Exception {
        String accessToken = obtainAccessToken();

        mockMvc.perform(post("/payments/payment")
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + accessToken + "invalidAccessToken")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(payment)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void givenValidAccessToken_whenGetSecureRequest_thenOk() throws Exception {
        String accessToken = obtainAccessToken();

        mockMvc.perform(post("/payments/payment")
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(payment)))
                .andExpect(status().isOk());
    }
}

package am.aua.cs322.orgchart.controllers;

import am.aua.cs322.orgchart.errors.ErrorMessages;
import am.aua.cs322.orgchart.services.InMemoryUserDetailsService;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AuthenticationControllerTest {
    @Autowired
    InMemoryUserDetailsService userDetailsService;

    private MockMvc mockMvc;

    private final String URL = "/authenticate";

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void authSuccessful() throws Exception {
        mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(List.of(
                        new BasicNameValuePair("username", "president"),
                        new BasicNameValuePair("password", "asd")))))

        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void authFailInvalidCredentials() throws Exception {
        mockMvc.perform(
                post(URL)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(EntityUtils.toString(new UrlEncodedFormEntity(List.of(
                                new BasicNameValuePair("username", "president"),
                                new BasicNameValuePair("password", "wrong_pass")))))

        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(ErrorMessages.INVALID_CREDENTIALS.name()));
    }

    @Test
    void authFailMissingPassword() throws Exception {
        mockMvc.perform(
                post(URL)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(EntityUtils.toString(new UrlEncodedFormEntity(List.of(
                                new BasicNameValuePair("username", "asd"),
                                new BasicNameValuePair("password", "")))))
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(ErrorMessages.MISSING_DATA.name()));
    }

    @Test
    void authFailMissingUsername() throws Exception {
        mockMvc.perform(
                post(URL)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(EntityUtils.toString(new UrlEncodedFormEntity(List.of(
                                new BasicNameValuePair("username", ""),
                                new BasicNameValuePair("password", "pass")))))
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(ErrorMessages.MISSING_DATA.name()));
    }

    @Test
    void authFailMissingData() throws Exception {
        mockMvc.perform(
                post(URL)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(EntityUtils.toString(new UrlEncodedFormEntity(List.of(
                                new BasicNameValuePair("username", ""),
                                new BasicNameValuePair("password", "")))))
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(ErrorMessages.MISSING_DATA.name()));
    }

    @Test
    void authFailNullCheck() throws Exception {
        mockMvc.perform(
                post(URL)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(ErrorMessages.MISSING_DATA.name()));
    }
}
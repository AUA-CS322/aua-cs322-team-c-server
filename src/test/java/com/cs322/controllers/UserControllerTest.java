package com.cs322.controllers;

import com.cs322.services.InMemoryUserDetailsService;
import com.jayway.jsonpath.JsonPath;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTest {

    @Autowired
    InMemoryUserDetailsService userDetailsService;

    private static MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    private static String token;

    @BeforeAll
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        MvcResult result = mockMvc.perform(post("/authenticate")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(List.of(
                        new BasicNameValuePair("username", "president"),
                        new BasicNameValuePair("password", "asd")))))
        ).andReturn();
        token = JsonPath.parse(result.getResponse().getContentAsString()).read("$.token");
    }


    @Test
    void getMeSuccessful() throws Exception {
        mockMvc.perform(
                get("/users/user")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .header("Authorization", "Bearer " + token)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("president"))
                .andExpect(jsonPath("$.email").value("president@aua.am"))
                .andExpect(jsonPath("$.firstName").value("FName"))
                .andExpect(jsonPath("$.lastName").value("LName"));
    }

    @Test
    void getUserSuccessful() throws Exception {
        mockMvc.perform(
                get("/users/member18")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .header("Authorization", "Bearer " + token)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("member18"))
                .andExpect(jsonPath("$.email").value("employee18@aua.am"))
                .andExpect(jsonPath("$.firstName").value("FName18"))
                .andExpect(jsonPath("$.lastName").value("LName18"));
    }

    @Test
    void getOrgChartSuccessful() throws Exception {
        mockMvc.perform(
                get("/org-chart/member14")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .header("Authorization", "Bearer " + token)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.parent").isNotEmpty())
                .andExpect(jsonPath("$.children").isNotEmpty());
    }
}

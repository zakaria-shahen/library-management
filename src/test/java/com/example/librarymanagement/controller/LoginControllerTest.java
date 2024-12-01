package com.example.librarymanagement.controller;

import com.example.librarymanagement.TestcontainersConfiguration;
import com.example.librarymanagement.dto.auth.LoginRequest;
import com.example.librarymanagement.dto.auth.LoginResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = { "app.jwt.expire-after-milliseconds=1000" })
@Import(TestcontainersConfiguration.class)
@AutoConfigureMockMvc
@Transactional
class LoginControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${app.jwt.expire-after-milliseconds}")
    long expireAfterMilliseconds;

    @Test
    void LoginWithCorrectCredentials() throws Exception {
        var LoginRequest = new LoginRequest("admin", "admin");
        MvcResult mvcResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(LoginRequest))
                ).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.expires").exists())
                .andDo(print())
                .andReturn();
        var response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), LoginResponse.class);
        Assertions.assertThat(response.accessToken()).isNotEmpty();
        Assertions.assertThat(response.refreshToken()).isNotEmpty();

        // verify access token
        mockMvc.perform(get("/patrons")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + response.accessToken())
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").isNotEmpty())
                .andDo(print());
    }

    @Test
    void LoginWithInvalidCredentialsThrowsException() throws Exception {
        var LoginRequest = new LoginRequest("wrong", "wrong");
        MvcResult mvcResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(LoginRequest))
                )
                .andExpect(status().isUnauthorized())
                .andDo(print())
                .andReturn();

    }

    @Test
    void CreateNewTokenFromRefreshToken() throws Exception {
        var LoginRequest = new LoginRequest("admin", "admin");
        MvcResult mvcResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(LoginRequest))
                ).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.expires").exists())
                .andDo(print())
                .andReturn();
        var response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), LoginResponse.class);

    }

}
package com.example.librarymanagement.controller;

import com.example.librarymanagement.TestcontainersConfiguration;
import com.example.librarymanagement.Users;
import com.example.librarymanagement.dto.UserDto;
import com.example.librarymanagement.security.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Import({TestcontainersConfiguration.class, SecurityConfig.class})
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void fetchAllUsers() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/patrons").with(Users.ADMIN))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").isNotEmpty())
                .andExpect(jsonPath("$.[0].username").isNotEmpty())
                .andDo(print())
                .andReturn();

        var response = objectMapper.readerForListOf(UserDto.class).readValue(mvcResult.getResponse().getContentAsString());
        Assertions.assertThat(response).isNotNull();
    }

    @Test
    void fetchUserById() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/patrons/{id}", 1).with(Users.USER_1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.username").isNotEmpty())
                .andDo(print())
                .andReturn();

        var response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserDto.class);
        Assertions.assertThat(response).isNotNull();
    }

    @Test
    void fetchUserByIdNotFound() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/patrons/{id}", 10000).with(Users.ADMIN))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn();
    }

    @Test
    void createUser() throws Exception {
        var userDto = new UserDto(1L, "test", "test", "test", "01111", "ADMIN");
        MvcResult mvcResult = mockMvc.perform(post("/patrons")
                        .with(Users.USER_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto))
                )
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andDo(print())
                .andReturn();

        verifyUserExists(mvcResult.getResponse().getHeader(HttpHeaders.LOCATION), "test");
    }

    @Test
    void updateUserIfExits() throws Exception {
        var userDto = new UserDto(1L, "test", "test", "test", "01111", "ADMIN");
        userDto.setName("update test");
        MvcResult mvcResult = mockMvc.perform(put("/patrons/{id}", userDto.getId())
                        .with(Users.USER_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto))
                )
                .andExpect(status().isNoContent())
                .andExpect(header().exists(HttpHeaders.CONTENT_LOCATION))
                .andDo(print())
                .andReturn();

        verifyUserExists(mvcResult.getResponse().getHeader(HttpHeaders.CONTENT_LOCATION), userDto.getUsername());
    }

    @Test
    void updateUserNotExitsThenCreateNewOne() throws Exception {
        var userDto = new UserDto(1000L, "test", "test", "test", "01111", "ADMIN");
        userDto.setName("update test 2");
        MvcResult mvcResult = mockMvc.perform(put("/patrons/{id}", userDto.getId())
                        .with(Users.ADMIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto))
                )
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.CONTENT_LOCATION))
                .andDo(print())
                .andReturn();

        verifyUserExists(mvcResult.getResponse().getHeader(HttpHeaders.CONTENT_LOCATION), userDto.getUsername());
    }

    @Test
    void deleteUserById() throws Exception {
        var id = 1L;
        MvcResult mvcResult = mockMvc.perform(delete("/patrons/{id}", id).with(Users.USER_1))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andReturn();

        MvcResult deleteResult = mockMvc.perform(get("/patrons/{id}", id).with(Users.ADMIN))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn();
    }

    private void verifyUserExists(String url, String username) throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(url).with(Users.ADMIN))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.username").value(username))
                .andDo(print())
                .andReturn();
        var response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserDto.class);
        Assertions.assertThat(response).isNotNull();
    }

}
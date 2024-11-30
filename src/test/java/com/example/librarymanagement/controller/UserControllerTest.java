package com.example.librarymanagement.controller;

import com.example.librarymanagement.TestcontainersConfiguration;
import com.example.librarymanagement.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@AutoConfigureMockMvc
@Transactional
@WithMockUser
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void fetchAllUsers() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/patrons"))
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
        MvcResult mvcResult = mockMvc.perform(get("/patrons/{id}", 1))
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
        MvcResult mvcResult = mockMvc.perform(get("/patrons/{id}", 10000))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn();
    }



}
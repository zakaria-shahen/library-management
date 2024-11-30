package com.example.librarymanagement.controller;

import com.example.librarymanagement.TestcontainersConfiguration;
import com.example.librarymanagement.dto.BookDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@Import(TestcontainersConfiguration.class)
@AutoConfigureMockMvc
@Transactional
@WithMockUser
class BookControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void fetchBooks() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/books"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").isNotEmpty())
                .andExpect(jsonPath("$.[0].title").isNotEmpty())
                .andDo(print())
                .andReturn();

        List<BookDto> response = objectMapper.readerForListOf(BookDto.class).readValue(mvcResult.getResponse().getContentAsString());
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.size()).isGreaterThan(0);
    }


    @Test
    void findBookById() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/books/{id}", 1))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").isNotEmpty())
                .andDo(print())
                .andReturn();
    }

    @Test
    void createBook() throws Exception {
        var book = new BookDto(1L, "Spring in action", "Craig Walls", Short.parseShort("2022"), "9781638356486", 15L);

        MvcResult mvcResult = mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book))
                ).andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andDo(print())
                .andReturn();

        verifyBookExists(mvcResult.getResponse().getHeader(HttpHeaders.LOCATION), book.getTitle());
    }

    private void verifyBookExists(String url, String title) throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(url))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value(title))
                .andDo(print())
                .andReturn();
        var response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), BookDto.class);
        Assertions.assertThat(response).isNotNull();
    }
}
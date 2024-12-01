package com.example.librarymanagement.controller;

import com.example.librarymanagement.TestcontainersConfiguration;
import com.example.librarymanagement.Users;
import com.example.librarymanagement.dto.BookDto;
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

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@Import(TestcontainersConfiguration.class)
@AutoConfigureMockMvc
@Transactional
class BookControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void fetchBooks() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/books").with(Users.ANONYMOUS))
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
        MvcResult mvcResult = mockMvc.perform(get("/books/{id}", 1).with(Users.ANONYMOUS))
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
                        .with(Users.ADMIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book))
                ).andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andDo(print())
                .andReturn();

        verifyBookExists(mvcResult.getResponse().getHeader(HttpHeaders.LOCATION), book.getTitle());
    }

    @Test
    void updateBookIfExists() throws Exception {
        var book = new BookDto(1L, "Spring in action", "Craig Walls", Short.parseShort("2022"), "9781638356486", 15L);
        book.setTitle("Spring in action 6th edition");

        MvcResult mvcResult = mockMvc.perform(put("/books/{id}", book.getId())
                        .with(Users.ADMIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book))
                ).andExpect(status().isNoContent())
                .andExpect(header().exists(HttpHeaders.CONTENT_LOCATION))
                .andDo(print())
                .andReturn();

        verifyBookExists(mvcResult.getResponse().getHeader(HttpHeaders.CONTENT_LOCATION), book.getTitle());
    }

    @Test
    void whenUpdateBookIsNotExistsThenCreateOne() throws Exception {
        var book = new BookDto(1000L, "dfdf", "dfd", Short.parseShort("2022"), "5515558356486", 10L);

        MvcResult mvcResult = mockMvc.perform(put("/books/{id}", book.getId())
                        .with(Users.ADMIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book))
                ).andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.CONTENT_LOCATION))
                .andDo(print())
                .andReturn();

        verifyBookExists(mvcResult.getResponse().getHeader(HttpHeaders.CONTENT_LOCATION), book.getTitle());
    }

    @Test
    void deleteBook() throws Exception {
        long id = 1;
        MvcResult mvcResult = mockMvc.perform(delete("/books/{id}", id).with(Users.ADMIN))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andReturn();

        var deleteResult = mockMvc.perform(get("/books/{id}", id))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    private void verifyBookExists(String url, String title) throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(url).with(Users.ANONYMOUS))
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
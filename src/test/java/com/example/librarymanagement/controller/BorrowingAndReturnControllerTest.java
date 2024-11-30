package com.example.librarymanagement.controller;

import com.example.librarymanagement.TestcontainersConfiguration;
import com.example.librarymanagement.dto.BorrowingDto;
import com.example.librarymanagement.exception.UnavailableBookCopiesException;
import com.example.librarymanagement.exception.UserBorrowedBookBeforeWithoutReturnIt;
import com.example.librarymanagement.model.BorrowingModel;
import com.example.librarymanagement.repository.BookRepository;
import com.example.librarymanagement.repository.BorrowingAndReturnRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Nested;
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

import java.time.LocalDate;
import java.util.stream.LongStream;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Import(TestcontainersConfiguration.class)
@AutoConfigureMockMvc
@Transactional
@WithMockUser
class BorrowingAndReturnControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    BorrowingAndReturnRepository borrowingAndReturnRepository;

    @Autowired
    BookRepository bookRepository;

    @Nested
    public class Borrowing {

        @Test
        void borrowingExistsBookAndAvailableBookCopies() throws Exception {
            var borrowing = new BorrowingDto(1L, LocalDate.ofYearDay(2025, 1), LocalDate.ofYearDay(2025, 31));
            MvcResult mvcResult = mockMvc.perform(post("/borrowing/{bookId}/user/{userId}", 2, 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(borrowing))
                    )
                    .andExpect(status().isCreated())
                    .andExpect(header().exists(HttpHeaders.LOCATION))
                    .andDo(print())
                    .andReturn();
        }

        @Test
        void borrowingNotExistsBookThenNotFound() throws Exception {
            var borrowing = new BorrowingDto(1L, LocalDate.ofYearDay(2025, 1), LocalDate.ofYearDay(2025, 31));
            MvcResult mvcResult = mockMvc.perform(post("/borrowing/{bookId}/user/{userId}", 1000, 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(borrowing))
                    )
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn();
        }

        @Test
        void borrowingExistsBookAndUnavailableBookCopiesThenConflict() throws Exception {
            // Given: update DB to be available copies is zero
            final var bookId = 1L;
            final var userId = 1L;
            final var copies = bookRepository.findById(bookId).get().getCopies();
            final var borrowingModel = new BorrowingModel(1L, bookId, userId, LocalDate.ofYearDay(2025, 1), LocalDate.ofYearDay(2025, 31), false);
            LongStream.range(1, copies).forEach(it ->{
                borrowingModel.setBorrowingDate(LocalDate.ofYearDay(2025, (int) it));
                borrowingAndReturnRepository.create(borrowingModel);
            });

            // when
            var borrowing = new BorrowingDto(1L, LocalDate.ofYearDay(2025, 1), LocalDate.ofYearDay(2025, 31));
            MvcResult mvcResult = mockMvc.perform(post("/borrowing/{bookId}/user/{userId}", bookId, userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(borrowing))
                    )
                    // then
                    .andExpect(status().isConflict())
                    .andExpect(status().reason(containsString(UnavailableBookCopiesException.DETAIL)))
                    .andDo(print())
                    .andReturn();
        }

        @Test
        void borrowingExistsBookAndAvailableBookCopiesButUserBorrowingItBeforeThenConflict() throws Exception {
            // Given: Make user Borrowing Book.
            final var bookId = 1L;
            final var userId = 1L;
            final var borrowingModel = new BorrowingModel(1L, bookId, userId, LocalDate.ofYearDay(2025, 1), LocalDate.ofYearDay(2025, 31), false);
            borrowingAndReturnRepository.create(borrowingModel);

            // when
            var borrowing = new BorrowingDto(1L, LocalDate.ofYearDay(2025, 10), LocalDate.ofYearDay(2025, 31));
            MvcResult mvcResult = mockMvc.perform(post("/borrowing/{bookId}/user/{userId}", bookId, userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(borrowing))
                    )
                    // then
                    .andExpect(status().isConflict())
                    .andExpect(status().reason(containsString(UserBorrowedBookBeforeWithoutReturnIt.DETAIL)))
                    .andDo(print())
                    .andReturn();
        }

    }


}

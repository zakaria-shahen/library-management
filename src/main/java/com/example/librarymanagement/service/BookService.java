package com.example.librarymanagement.service;

import com.example.librarymanagement.controller.mapper.BookMapper;
import com.example.librarymanagement.dto.BookDto;
import com.example.librarymanagement.repository.BookRepository;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public @NonNull List<BookDto> findAll() {
        return BookMapper.INSTANCE.toBookDto(bookRepository.findAll());
    }
}

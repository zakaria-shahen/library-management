package com.example.librarymanagement.service;

import com.example.librarymanagement.controller.mapper.BookMapper;
import com.example.librarymanagement.dto.BookDto;
import com.example.librarymanagement.exception.NotFoundResourceException;
import com.example.librarymanagement.repository.BookRepository;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public @NonNull List<BookDto> findAll() {
        return BookMapper.INSTANCE.toBookDto(bookRepository.findAll());
    }

    public @NonNull BookDto findById(long id) {
        return BookMapper.INSTANCE.toBookDto(
                bookRepository.findById(id)
                        .orElseThrow(NotFoundResourceException::new)
        );
    }


    public @NonNull BookDto create(@NonNull BookDto bookDto) {
        var model = BookMapper.INSTANCE.toBookModel(bookDto);
        bookDto.setId(bookRepository.create(model).getId());
        return bookDto;
    }

    public @NonNull Optional<BookDto> update(@NonNull BookDto bookDto) {
        var model = BookMapper.INSTANCE.toBookModel(bookDto);
        var isUpdated = bookRepository.update(model);
        if (isUpdated == 1) {
             return Optional.empty();
        }

        return Optional.of(create(bookDto));
    }
}

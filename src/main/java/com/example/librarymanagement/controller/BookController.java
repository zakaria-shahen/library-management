package com.example.librarymanagement.controller;

import com.example.librarymanagement.dto.BookDto;
import com.example.librarymanagement.service.BookService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/books")
@AllArgsConstructor
public class BookController {

    private BookService bookService;

    @GetMapping
    public List<BookDto> getBooks() {
        return bookService.findAll();
    }

    @GetMapping("/{id}")
    public BookDto getBook(@PathVariable long id) {
        return bookService.findById(id);
    }


    @PostMapping
    public ResponseEntity<Void> createBook(@RequestBody BookDto bookDto) {
        var uri = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/" + bookService.create(bookDto).getId())
                .build().toUri();

        return ResponseEntity.created(uri).build();
    }

}

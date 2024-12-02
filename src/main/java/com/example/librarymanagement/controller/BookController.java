package com.example.librarymanagement.controller;

import com.example.librarymanagement.dto.BookDto;
import com.example.librarymanagement.service.BookService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Optional;

import static com.example.librarymanagement.controller.ControllerUtils.URIRequestAndReplaceLastPath;

@RestController
@RequestMapping("/books")
@AllArgsConstructor
public class BookController {

    private BookService bookService;

    @GetMapping
    @Cacheable("getBooks")
    public List<BookDto> getBooks() {
        return bookService.findAll();
    }

    @GetMapping("/{id}")
    public BookDto getBook(@PathVariable long id) {
        return bookService.findById(id);
    }


    @PostMapping
    public ResponseEntity<Void> createBook(@RequestBody @Validated BookDto bookDto) {
        var uri = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/" + bookService.create(bookDto).getId())
                .build().toUri();

        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateBook(@RequestBody @Validated BookDto bookDto, @PathVariable long id) {
        bookDto.setId(id);
        var uri = ServletUriComponentsBuilder.fromCurrentRequestUri();
        Optional<BookDto> book = bookService.update(bookDto);

        return book.<ResponseEntity<Void>>map(dto ->
                ResponseEntity.status(HttpStatus.CREATED)
                        .header(HttpHeaders.CONTENT_LOCATION, URIRequestAndReplaceLastPath("/" + dto.getId()))
                .build()
        ).orElseGet(() ->
                ResponseEntity.noContent()
                .header(HttpHeaders.CONTENT_LOCATION, URIRequestAndReplaceLastPath("/" + bookDto.getId()))
                .build()
        );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable long id) {
        bookService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}

package com.example.librarymanagement.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Year;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookDto {
    private long id;
    @NotEmpty
    private String title;
    @NotEmpty
    private String author;
    private short publication;
    @NotEmpty
    private String isbn;
    @Positive
    private long copies;
}

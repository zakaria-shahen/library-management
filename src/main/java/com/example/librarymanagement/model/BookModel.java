package com.example.librarymanagement.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookModel {
    private long id;
    private String title;
    private String author;
    private short publication;
    private String isbn;
    private long copies;
}

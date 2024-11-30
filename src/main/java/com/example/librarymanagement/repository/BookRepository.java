package com.example.librarymanagement.repository;

import com.example.librarymanagement.model.BookModel;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class BookRepository {

    private final JdbcClient jdbcClient;

    public @NonNull List<BookModel> findAll() {
        return jdbcClient.sql("select id, title, author, publication, ISBN, copies from book")
                .query(BookModel.class)
                .list();
    }

    public @NonNull Optional<BookModel> findById(long id) {
        return jdbcClient.sql("select id, title, author, publication, ISBN, copies from book where id = ?")
                .param(id)
                .query(BookModel.class)
                .optional();
    }
}

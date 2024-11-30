package com.example.librarymanagement.repository;

import com.example.librarymanagement.model.BookModel;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class BookRepository {

    private final JdbcClient jdbcClient;

    public @NonNull List<BookModel> findAll() {
        return jdbcClient.sql("select id, title, author, publication, isbn, copies from book where is_deleted = false")
                .query(BookModel.class)
                .list();
    }

    public @NonNull Optional<BookModel> findById(long id) {
        return jdbcClient.sql("select id, title, author, publication, isbn, copies from book where id = ? and is_deleted = false")
                .param(id)
                .query(BookModel.class)
                .optional();
    }

    public @NonNull BookModel create(@NonNull BookModel model) {
        var keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql("insert into book(title, author, publication, isbn, copies) values (:title, :author, :publication, :isbn, :copies)")
                .paramSource(model)
                .update(keyHolder);
        model.setId(keyHolder.getKey().longValue());
        return model;
    }

    /**
     *
     * @param model: BookModel
     * @return number of affected records
     */
    public int update(BookModel model) {
        return jdbcClient
                .sql("update book set title = :title, author = :author, publication = :publication, isbn = :isbn, copies = :copies where id = :id and is_deleted = false")
                .paramSource(model)
                .update();
    }


    public void deleteById(long id) {
        jdbcClient.sql("update book set is_deleted = true where id = ?")
                .param(id)
                .update();
    }
}

package com.example.librarymanagement.repository;

import com.example.librarymanagement.model.BorrowingModel;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@AllArgsConstructor
@Repository
public class BorrowingAndReturnRepository {

    private final JdbcClient jdbcClient;


    public long findCopiesCopiesByBookId(long bookId) {
        return jdbcClient.sql("select count(*) from borrowing where book_id = ? and is_returned = false and is_deleted = false")
                .param(bookId)
                .query(Long.class)
                .single();
    }

    public @NonNull BorrowingModel create(@NonNull BorrowingModel model) {
        var keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql("insert into borrowing(book_id, user_id, borrowing_date, return_date, is_returned) values(:bookId, :userId, :borrowingDate, :returnDate, :isReturned)")
                .paramSource(model)
                .update(keyHolder);
        model.setId(keyHolder.getKey().longValue());
        return model;
    }

    public int findBorrowingByBookIdAndUserId(long bookId, long userId) {
        return jdbcClient.sql("select count(*) from borrowing where book_id = ? and user_id = ? and is_returned = false and is_deleted = false")
                .param(bookId)
                .param(userId)
                .query(Integer.class)
                .single();
    }

    /**
     *
     * @param bookId book id
     * @param userId user id
     * @return number of affected rows
     */
    public int returns(long bookId, long userId) {
        return jdbcClient.sql("update borrowing set is_returned = true where book_id = ? and user_id = ? and is_deleted = false")
                .param(bookId)
                .param(userId)
                .update();
    }
}

package com.example.librarymanagement.repository;

import com.example.librarymanagement.model.UserModel;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class UserRepository {
    private final JdbcClient jdbcClient;


    public List<UserModel> findAll() {
        return jdbcClient.sql("select id, name, username, password, phone_number, role from user where is_deleted = false")
                .query(UserModel.class)
                .list();
    }

    public @NonNull Optional<UserModel> findById(long id) {
        return jdbcClient.sql("select id, name, username, password, phone_number, role from user where id = ? and is_deleted = false")
                .param(id)
                .query(UserModel.class)
                .optional();
    }

    public @NonNull UserModel create(@NonNull UserModel model) {
        var keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql("insert into user(name, username, password, phone_number, role) values(:name, :username, :password, :phoneNumber, :role)")
                .paramSource(model)
                .update(keyHolder);
        model.setId(keyHolder.getKey().longValue());
        return model;
    }


    /**
     *  update entire user model if user id exists.
     * @param userModel model
     * @return number of affected rows
     */
    public int update(@NonNull UserModel userModel) {
        return jdbcClient
                .sql("update user set name = :name, username = :username, password = :password, phone_number = :phoneNumber where id = :id and is_deleted = false")
                .paramSource(userModel)
                .update();
    }

    public void deleteById(long id) {
        jdbcClient.sql("update user set is_deleted = true where id = ?")
                .param(id)
                .update();
    }

    public @NonNull Optional<UserModel> findByUsername(@NonNull String username) {
        return jdbcClient.sql("select id, name, username, password, phone_number, role from user where username = ? and is_deleted = false")
                .param(username)
                .query(UserModel.class)
                .optional();
    }
}

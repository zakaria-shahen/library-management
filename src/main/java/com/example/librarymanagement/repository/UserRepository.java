package com.example.librarymanagement.repository;

import com.example.librarymanagement.model.UserModel;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class UserRepository {
    private final JdbcClient jdbcClient;


    public List<UserModel> findAll() {
        return jdbcClient.sql("select id, name, username, password, phone_number, role from user")
                .query(UserModel.class)
                .list();
    }
}

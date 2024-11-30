package com.example.librarymanagement.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserModel {
    private long id;
    private String name;
    private String username;
    private String password;
    private String phoneNumber;
    private String role;
}

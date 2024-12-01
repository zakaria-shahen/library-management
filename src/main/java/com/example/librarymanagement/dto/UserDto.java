package com.example.librarymanagement.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDto {
    private long id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
    @NotEmpty
    private String phoneNumber;
    private String role;
}

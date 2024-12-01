package com.example.librarymanagement.dto.auth;

public record LoginRequest(
        String username,
        String password
) {}

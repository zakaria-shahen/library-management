package com.example.librarymanagement.dto.auth;

import jakarta.validation.constraints.NotEmpty;

public record LoginRequest(
        @NotEmpty
        String username,
        @NotEmpty
        String password
) {}

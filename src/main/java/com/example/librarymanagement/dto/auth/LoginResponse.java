package com.example.librarymanagement.dto.auth;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long expires //  seconds
) {

}
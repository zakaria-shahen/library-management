package com.example.librarymanagement.dto.auth;

import com.fasterxml.jackson.annotation.JsonGetter;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        OAuth2AccessToken.TokenType tokenType,
        Long expires //  seconds
) {
    @JsonGetter
    public String getTokenType() {
        return tokenType.getValue();
    }

}
package com.example.librarymanagement.controller;


import com.example.librarymanagement.dto.auth.LoginRequest;
import com.example.librarymanagement.dto.auth.LoginResponse;
import com.example.librarymanagement.exception.AuthInvalidException;
import com.example.librarymanagement.security.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        return authenticationService.authentication(loginRequest);
    }

    @PostMapping("/token")
    public LoginResponse newToken(
            @RequestParam(value = "grant_type", defaultValue = "refresh_token")
            String grantType,
            @RequestParam("refresh_token")
            String refreshToken,
            @RequestParam(value = "client_id", defaultValue = "web")
            String clientId,
            @RequestParam(value = "client_secret", defaultValue = "secret")
            String clientSecret
    ) {
        if (!Objects.equals(grantType, "refresh_token") || !Objects.equals(clientId, "web") || !Objects.equals(clientSecret, "secret")) {
            throw new AuthInvalidException();
        }

        return authenticationService.authenticationForRefreshToken(refreshToken);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestParam("refresh_token") String refreshToken, Authentication authentication) {
        var accessToken = ((AbstractOAuth2TokenAuthenticationToken<?>) authentication).getToken().getTokenValue();
        authenticationService.logout(accessToken, refreshToken);
    }


}

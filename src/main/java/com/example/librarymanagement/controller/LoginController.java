package com.example.librarymanagement.controller;


import com.example.librarymanagement.dto.auth.LoginRequest;
import com.example.librarymanagement.dto.auth.LoginResponse;
import com.example.librarymanagement.security.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class LoginController {

    private AuthenticationService authenticationService;


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
//            throw new AuthInvalidException();
        }
    return null;
//        return authenticationService.refreshTokenGrantType(refreshToken);
    }



}

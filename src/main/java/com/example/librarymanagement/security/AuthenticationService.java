package com.example.librarymanagement.security;

import com.example.librarymanagement.dto.auth.LoginRequest;
import com.example.librarymanagement.dto.auth.LoginResponse;
import com.example.librarymanagement.exception.AuthInvalidException;
import com.example.librarymanagement.model.UserModel;
import com.example.librarymanagement.repository.UserRepository;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${app.jwt.expire-after-milliseconds:36000}")
    private long expireAfterMillis;

    public LoginResponse authentication(LoginRequest loginRequest) {
        return userRepository.findByUsername(loginRequest.username()).map(it -> {
            if (!passwordEncoder.matches(loginRequest.password(), it.getPassword())) {
                throw  new AuthInvalidException();
            }

            return new LoginResponse(
                    jwtService.generateToken(expireAfterMillis, false,  it),
                    jwtService.generateToken(expireAfterMillis, true, it),
                    OAuth2AccessToken.TokenType.BEARER.getValue(),
                    expireAfterMillis
            );

        }).orElseThrow(AuthInvalidException::new);
    }


    public LoginResponse authenticationForRefreshToken(String refreshToken) {
        JWTClaimsSet claims = jwtService.verifyRefreshTokenAndGetClaims(refreshToken);

        UserModel userModel = new UserModel(
                Long.parseLong(claims.getSubject()),
                claims.getClaim("name").toString(),
                claims.getClaim("username").toString(),
                null,
                null,
                claims.getAudience().getFirst()
        );

        return new LoginResponse(
                jwtService.generateToken(expireAfterMillis, false, userModel),
                jwtService.generateToken(expireAfterMillis, true, userModel),
                OAuth2AccessToken.TokenType.BEARER.getValue(),
                expireAfterMillis
        );
    }


}

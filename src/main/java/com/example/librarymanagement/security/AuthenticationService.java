package com.example.librarymanagement.security;

import com.example.librarymanagement.dto.auth.LoginRequest;
import com.example.librarymanagement.dto.auth.LoginResponse;
import com.example.librarymanagement.exception.AuthInvalidException;
import com.example.librarymanagement.model.BlockedTokenModel;
import com.example.librarymanagement.model.UserModel;
import com.example.librarymanagement.repository.BlockedTokenRepository;
import com.example.librarymanagement.repository.UserRepository;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;

@RequiredArgsConstructor
@Component
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final BlockedTokenRepository blockedTokenRepository;

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

    @Transactional
    public LoginResponse authenticationForRefreshToken(@NonNull String refreshToken) {
        if (refreshToken.isEmpty() || blockedTokenRepository.existsById(refreshToken)) {
            throw new AuthInvalidException();
        }

        JWTClaimsSet claims = jwtService.verifyRefreshTokenAndGetClaims(refreshToken);
        UserModel userModel;
        try {
            userModel = new UserModel(
                    Long.parseLong(claims.getSubject()),
                    claims.getClaim("name").toString(),
                    claims.getClaim("username").toString(),
                    null,
                    null,
                    claims.getListClaim("scope").getFirst().toString()
            );
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return new LoginResponse(
                jwtService.generateToken(expireAfterMillis, false, userModel),
                jwtService.generateToken(expireAfterMillis, true, userModel),
                OAuth2AccessToken.TokenType.BEARER.getValue(),
                expireAfterMillis
        );
    }

    @Transactional
    public void logout(@NonNull String accessToken, @NonNull String refreshToken) {
        if (refreshToken.isEmpty()) {
            throw new AuthInvalidException();
        }

        blockedTokenRepository.save(new BlockedTokenModel(refreshToken));
        blockedTokenRepository.save(new BlockedTokenModel(accessToken));
    }
}

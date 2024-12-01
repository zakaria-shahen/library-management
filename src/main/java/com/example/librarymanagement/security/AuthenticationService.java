package com.example.librarymanagement.security;

import com.example.librarymanagement.dto.auth.LoginRequest;
import com.example.librarymanagement.dto.auth.LoginResponse;
import com.example.librarymanagement.exception.AuthInvalidException;
import com.example.librarymanagement.model.UserModel;
import com.example.librarymanagement.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Getter
@Component
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RSAKey defaultRSKeyAccessToken;
    private final RSAKey defaultRSKeyRefreshToken;

    @Value("${app.jwt.expire-after-milliseconds:36000}")
    private long expireAfterMillis;

    public LoginResponse authentication(LoginRequest loginRequest) {
        return userRepository.findByUsername(loginRequest.username()).map(it -> {
            if (!passwordEncoder.matches(loginRequest.password(), it.getPassword())) {
                throw  new AuthInvalidException();
            }

            return new LoginResponse(
                    generateToken(expireAfterMillis, false,  it),
                    generateToken(expireAfterMillis, true, it),
                    OAuth2AccessToken.TokenType.BEARER.getValue(),
                    expireAfterMillis
            );

        }).orElseThrow(AuthInvalidException::new);
    }


    public LoginResponse authenticationForRefreshToken(String refreshToken) {
        JWTClaimsSet claims;
        try {
            var jwt = SignedJWT.parse(refreshToken);

            JWSVerifier verifier = new RSASSAVerifier(defaultRSKeyRefreshToken);
            if (!jwt.verify(verifier)) {
                throw new AuthInvalidException();
            }
            claims = jwt.getJWTClaimsSet();

        } catch (JOSEException | ParseException e) {
            throw new AuthInvalidException();
        }
        UserModel userModel = new UserModel(
                Long.parseLong(claims.getSubject()),
                claims.getClaim("name").toString(),
                claims.getClaim("username").toString(),
                null,
                null,
                claims.getAudience().getFirst()
        );

        return new LoginResponse(
                generateToken(expireAfterMillis, false, userModel),
                generateToken(expireAfterMillis, true, userModel),
                OAuth2AccessToken.TokenType.BEARER.getValue(),
                expireAfterMillis
        );
    }


    private String generateToken(long expireAfterMillis, boolean isRefreshToken, UserModel userModel) {
        var issueTime = LocalDateTime.now().atZone(ZoneOffset.systemDefault()).toInstant();
        var expirationTime = issueTime.plusMillis(expireAfterMillis);
        var uri = ServletUriComponentsBuilder.fromCurrentContextPath().replacePath("/");
        var claims = new JWTClaimsSet.Builder()
                .issuer(uri.toUriString())
                .audience(uri.replacePath("/front-end").toUriString())
                .claim("scope", List.of(userModel.getRole()))
                .subject(String.valueOf(userModel.getId()))
                .claim("username", userModel.getUsername())
                .claim("name", userModel.getName());

        if (isRefreshToken) {
            claims.notBeforeTime(Date.from(expirationTime.plusMillis(10L)))
                .issueTime(Date.from(expirationTime))
                .expirationTime(Date.from(expirationTime.plusMillis(expireAfterMillis + 10)));
            return generateJwt(claims.build(), defaultRSKeyRefreshToken);
        }

        claims.issueTime(Date.from(issueTime))
                .expirationTime(Date.from(expirationTime));
        return generateJwt(claims.build(), defaultRSKeyAccessToken);

    }


    private String generateJwt(JWTClaimsSet claims, RSAKey rsaKey) {
        try {
            JWSSigner signer = new RSASSASigner(rsaKey.toRSAKey());
            JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.RS512)
                    .keyID(rsaKey.getKeyID())
                    .build();

            SignedJWT signedJWT = new SignedJWT(jwsHeader, claims);
            signedJWT.sign(signer);

            return signedJWT.serialize();

        } catch (JOSEException ex) {
            throw new AuthInvalidException();
        }

    }


}

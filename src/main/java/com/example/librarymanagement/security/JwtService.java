package com.example.librarymanagement.security;

import com.example.librarymanagement.exception.AuthInvalidException;
import com.example.librarymanagement.model.UserModel;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;


@AllArgsConstructor
@Component
public class JwtService {

    private final RSAKey defaultRSKeyAccessToken;
    private final RSAKey defaultRSKeyRefreshToken;

    @NonNull
    public String generateToken(long expireAfterMillis, boolean isRefreshToken, @NonNull UserModel userModel) {
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
            claims.notBeforeTime(Date.from(expirationTime))
                    .issueTime(Date.from(expirationTime))
                    .expirationTime(Date.from(expirationTime.plusMillis(expireAfterMillis + 10)));
            return generateJwt(claims.build(), defaultRSKeyRefreshToken);
        }

        claims.issueTime(Date.from(issueTime))
                .expirationTime(Date.from(expirationTime));
        return generateJwt(claims.build(), defaultRSKeyAccessToken);

    }


    @NonNull
    private String generateJwt(@NonNull JWTClaimsSet claims, @NonNull RSAKey rsaKey) {
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

    @NonNull
    public JWTClaimsSet verifyRefreshTokenAndGetClaims(@NonNull String refreshToken) {
        try {
            var jwt = SignedJWT.parse(refreshToken);

            JWSVerifier verifier = new RSASSAVerifier(defaultRSKeyRefreshToken);

            Instant now = Instant.now();
            if (!jwt.verify(verifier) || jwt.getJWTClaimsSet().getNotBeforeTime().toInstant().isAfter(now)) {
                throw new AuthInvalidException();
            }


            return jwt.getJWTClaimsSet();

        } catch (JOSEException | ParseException ex) {
            throw new AuthInvalidException();
        }

    }
}

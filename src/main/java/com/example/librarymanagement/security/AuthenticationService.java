package com.example.librarymanagement.security;

import com.example.librarymanagement.dto.auth.LoginRequest;
import com.example.librarymanagement.dto.auth.LoginResponse;
import com.example.librarymanagement.exception.AuthInvalidException;
import com.example.librarymanagement.model.UserModel;
import com.example.librarymanagement.repository.UserRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Getter
@Component
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RSAKey defaultRasKey;

    private final static long EXPIRE_AFTER_MINUTES = 30;

    public LoginResponse authentication(LoginRequest loginRequest) {
        return userRepository.findByUsername(loginRequest.username()).map(it -> {
            if (!passwordEncoder.matches(loginRequest.password(), it.getPassword())) {
                throw  new AuthInvalidException();
            }

            return new LoginResponse(
                    generateToken(EXPIRE_AFTER_MINUTES, false,  it),
                    generateToken(EXPIRE_AFTER_MINUTES, true, it),
                    OAuth2AccessToken.TokenType.BEARER,
                    EXPIRE_AFTER_MINUTES
            );

        }).orElseThrow(AuthInvalidException::new);
    }

    private String generateToken(long expireAfterMinutes, boolean isRefreshToken, UserModel userModel) {
        var issueTime = LocalDateTime.now();
        var expirationTime = issueTime.plusMinutes(expireAfterMinutes);
        var uri = ServletUriComponentsBuilder.fromCurrentContextPath().replacePath("/");
        var claims = new JWTClaimsSet.Builder()
                .issuer(uri.toUriString())
                .audience(uri.replacePath("/front-end").toUriString())
                .claim("scope", List.of(userModel.getRole()))
                .subject(String.valueOf(userModel.getId()))
                .claim("username", userModel.getUsername())
                .claim("name", userModel.getName());

        if (isRefreshToken) {
            claims.notBeforeTime(convertToDate(expirationTime.plusMinutes(1)))
                .issueTime(convertToDate(expirationTime))
                .expirationTime(convertToDate(expirationTime.plusMinutes(expireAfterMinutes)));
        } else {
            claims.issueTime(convertToDate(issueTime))
                .expirationTime(convertToDate(expirationTime));
        }


        return generateJwt(claims.build());

    }


    private String generateJwt(JWTClaimsSet claims) {
        try {
            JWSSigner signer = new RSASSASigner(defaultRasKey.toRSAKey());
            JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.RS512)
                    .keyID(defaultRasKey.getKeyID())
                    .build();

            SignedJWT signedJWT = new SignedJWT(jwsHeader, claims);
            signedJWT.sign(signer);

            return signedJWT.serialize();

        } catch (JOSEException ex) {
            throw new AuthInvalidException();
        }

    }

    private Date convertToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.plusMinutes(30L).atZone(ZoneId.systemDefault()).toInstant());
    }


}

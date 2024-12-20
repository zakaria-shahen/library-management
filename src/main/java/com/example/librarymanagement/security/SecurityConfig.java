package com.example.librarymanagement.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Map;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(it ->
                        it.requestMatchers("/auth/login").anonymous()
                                .requestMatchers("/auth/token", "/health").permitAll()
                                .requestMatchers(HttpMethod.GET, "/books/**").permitAll()
                                .requestMatchers("/books/**").hasAuthority("SCOPE_ADMIN")
                                .requestMatchers(HttpMethod.GET, "/patrons").hasAuthority("SCOPE_ADMIN")
                                .requestMatchers(HttpMethod.POST, "/patrons").permitAll()
                                .anyRequest().authenticated()
                ).csrf(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public RSAKey defaultRSKeyAccessToken(@Value("classpath:jwk-generate-token.json") Resource jwkGenerateToken) throws ParseException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> m = objectMapper.readerForMapOf(Object.class)
                .readValue(jwkGenerateToken.getContentAsString(Charset.defaultCharset()));
        return RSAKey.parse(m);
    }

    @Bean
    public RSAKey defaultRSKeyRefreshToken(@Value("classpath:jwk-refresh-token.json") Resource jwkGenerateToken) throws ParseException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> m = objectMapper.readerForMapOf(Object.class)
                .readValue(jwkGenerateToken.getContentAsString(Charset.defaultCharset()));
        return RSAKey.parse(m);
    }
}

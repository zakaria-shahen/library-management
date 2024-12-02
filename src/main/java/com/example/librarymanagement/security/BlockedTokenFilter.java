package com.example.librarymanagement.security;
import com.example.librarymanagement.exception.AuthInvalidException;
import com.example.librarymanagement.repository.BlockedTokenRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@AllArgsConstructor
@Component
public class BlockedTokenFilter extends OncePerRequestFilter {

    private final BlockedTokenRepository blockedTokenRepository;
    private final int PREFIX_BEARER_LENGTH = "Bearer ".length() + 1;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (token == null || token.isEmpty() || token.length() < PREFIX_BEARER_LENGTH) {
            filterChain.doFilter(request, response);
            return;
        }

        token = token.substring(PREFIX_BEARER_LENGTH);

        if (blockedTokenRepository.existsById(token)) {
            throw new AuthInvalidException();
        }

        filterChain.doFilter(request, response);
    }
}

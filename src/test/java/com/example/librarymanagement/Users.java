package com.example.librarymanagement;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

final public class Users {
    private Users() {
    }

    public static final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor ADMIN = SecurityMockMvcRequestPostProcessors.jwt()
            .jwt(jwt -> jwt.claim("aud", "1"))
            .authorities(new SimpleGrantedAuthority("SCOPE_ADMIN"));

    public static final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor USER_1 = SecurityMockMvcRequestPostProcessors.jwt()
            .jwt(jwt -> jwt.claim("aud", "1"))
            .authorities(new SimpleGrantedAuthority("SCOPE_ADMIN"));

    public static final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor USER_1000 = SecurityMockMvcRequestPostProcessors.jwt()
            .jwt(jwt -> jwt.claim("aud", "100"))
            .authorities(new SimpleGrantedAuthority("SCOPE_ADMIN"));

    public static final RequestPostProcessor ANONYMOUS = SecurityMockMvcRequestPostProcessors.anonymous();
}
package com.example.taskmanager.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SecurityConfigTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void passwordEncoderBean_ShouldBeBCrypt() {
        PasswordEncoder encoder = context.getBean(PasswordEncoder.class);
        assertNotNull(encoder);
        assertTrue(encoder instanceof PasswordEncoder);
        String raw = "password123";
        String encoded = encoder.encode(raw);
        assertTrue(encoder.matches(raw, encoded));
    }

    @Test
    void authenticationManagerBean_ShouldExist() throws Exception {
        AuthenticationManager manager = context.getBean(AuthenticationManager.class);
        assertNotNull(manager);
    }

    @Test
    void securityFilterChainBean_ShouldExist() {
        SecurityFilterChain chain = context.getBean(SecurityFilterChain.class);
        assertNotNull(chain);
    }
}

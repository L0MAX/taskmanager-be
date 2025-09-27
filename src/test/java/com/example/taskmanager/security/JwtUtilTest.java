package com.example.taskmanager.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String secret = "01234567890123456789012345678901";
    private final long expirationMs = 3600000;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(secret, expirationMs);
        jwtUtil.init();
    }

    @Test
    void generateToken_ShouldReturnNonNullToken() {
        String token = jwtUtil.generateToken("testUser");
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void getUsernameFromToken_ShouldReturnCorrectUsername() {
        String token = jwtUtil.generateToken("testUser");
        String username = jwtUtil.getUsernameFromToken(token);
        assertEquals("testUser", username);
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() {
        String token = jwtUtil.generateToken("testUser");
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    void validateToken_WithInvalidToken_ShouldReturnFalse() {
        String invalidToken = "invalid.token.here";
        assertFalse(jwtUtil.validateToken(invalidToken));
    }

    @Test
    void validateToken_WithExpiredToken_ShouldReturnFalse() throws InterruptedException {
        JwtUtil shortJwt = new JwtUtil(secret, 10);
        shortJwt.init();
        String token = shortJwt.generateToken("user");
        Thread.sleep(20);
        assertFalse(shortJwt.validateToken(token));
    }

    @Test
    void init_WithBlankSecret_ShouldThrowException() {
        JwtUtil blankJwt = new JwtUtil("   ", expirationMs);
        Exception exception = assertThrows(IllegalStateException.class, blankJwt::init);
        assertTrue(exception.getMessage().contains("jwt.secret is not configured"));
    }
}

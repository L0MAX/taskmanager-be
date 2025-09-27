package com.example.taskmanager.payload.response;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthResponseTest {

    @Test
    void testConstructorAndGetter() {
        String token = "dummy-jwt-token";
        AuthResponse response = new AuthResponse(token);

        assertEquals(token, response.getToken());
    }

    @Test
    void testSetter() {
        AuthResponse response = new AuthResponse();
        String token = "another-token";

        response.setToken(token);

        assertEquals(token, response.getToken());
    }
}

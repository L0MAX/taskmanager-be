package com.example.taskmanager.payload.request;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    @Test
    void testGettersAndSetters() {
        LoginRequest request = new LoginRequest();

        request.setUsername("testUser");
        request.setPassword("secretPassword");

        assertEquals("testUser", request.getUsername());
        assertEquals("secretPassword", request.getPassword());
    }

    @Test
    void testDefaultConstructor() {
        LoginRequest request = new LoginRequest();
        assertNull(request.getUsername());
        assertNull(request.getPassword());
    }
}

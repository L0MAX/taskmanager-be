package com.example.taskmanager.payload.request;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestTest {

    @Test
    void testGettersAndSetters() {
        RegisterRequest request = new RegisterRequest();

        request.setUsername("newUser");
        request.setPassword("newPassword");

        assertEquals("newUser", request.getUsername());
        assertEquals("newPassword", request.getPassword());
    }

    @Test
    void testDefaultConstructor() {
        RegisterRequest request = new RegisterRequest();
        assertNull(request.getUsername());
        assertNull(request.getPassword());
    }
}

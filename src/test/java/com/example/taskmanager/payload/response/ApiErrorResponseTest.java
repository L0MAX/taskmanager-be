package com.example.taskmanager.payload.response;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class ApiErrorResponseTest {

    @Test
    void testConstructorAndGetters() {
        int status = 404;
        String message = "Resource not found";

        ApiErrorResponse response = new ApiErrorResponse(status, message);

        assertEquals(status, response.getStatus());
        assertEquals(message, response.getMessage());
        assertNotNull(response.getTimestamp());
        assertTrue(response.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testSetters() {
        ApiErrorResponse response = new ApiErrorResponse(500, "Server error");

        response.setStatus(400);
        response.setMessage("Bad request");
        LocalDateTime now = LocalDateTime.now();
        response.setTimestamp(now);

        assertEquals(400, response.getStatus());
        assertEquals("Bad request", response.getMessage());
        assertEquals(now, response.getTimestamp());
    }
}

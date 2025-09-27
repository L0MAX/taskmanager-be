package com.example.taskmanager.payload.response;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskResponseTest {

    @Test
    void testConstructorAndGetters() {
        Long id = 1L;
        String title = "Test Task";
        String description = "Task description";
        String status = "PENDING";
        String createdAt = "2025-09-27T10:00:00";
        String updatedAt = "2025-09-27T12:00:00";

        TaskResponse response = new TaskResponse(id, title, description, status, createdAt, updatedAt);

        assertEquals(id, response.getId());
        assertEquals(title, response.getTitle());
        assertEquals(description, response.getDescription());
        assertEquals(status, response.getStatus());
        assertEquals(createdAt, response.getCreatedAt());
        assertEquals(updatedAt, response.getUpdatedAt());
    }

    @Test
    void testSetters() {
        TaskResponse response = new TaskResponse();

        Long id = 2L;
        String title = "Another Task";
        String description = "Another description";
        String status = "COMPLETED";
        String createdAt = "2025-09-27T08:00:00";
        String updatedAt = "2025-09-27T09:30:00";

        response.setId(id);
        response.setTitle(title);
        response.setDescription(description);
        response.setStatus(status);
        response.setCreatedAt(createdAt);
        response.setUpdatedAt(updatedAt);

        assertEquals(id, response.getId());
        assertEquals(title, response.getTitle());
        assertEquals(description, response.getDescription());
        assertEquals(status, response.getStatus());
        assertEquals(createdAt, response.getCreatedAt());
        assertEquals(updatedAt, response.getUpdatedAt());
    }
}

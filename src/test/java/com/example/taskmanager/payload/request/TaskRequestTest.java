package com.example.taskmanager.payload.request;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskRequestTest {

    @Test
    void testGettersAndSetters() {
        TaskRequest request = new TaskRequest();

        request.setTitle("My Task");
        request.setDescription("Task description");
        request.setStatus("PENDING");

        assertEquals("My Task", request.getTitle());
        assertEquals("Task description", request.getDescription());
        assertEquals("PENDING", request.getStatus());
    }

    @Test
    void testDefaultConstructor() {
        TaskRequest request = new TaskRequest();
        assertNull(request.getTitle());
        assertNull(request.getDescription());
        assertNull(request.getStatus());
    }
}

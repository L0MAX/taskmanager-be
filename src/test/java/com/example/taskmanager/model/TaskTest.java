package com.example.taskmanager.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    private User mockUser;

    @BeforeEach
    void setup() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testUser");
    }

    @Test
    void testDefaultConstructorAndSetters() {
        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setStatus(Task.Status.COMPLETED);
        task.setUser(mockUser);

        assertEquals("Test Task", task.getTitle());
        assertEquals("Test Description", task.getDescription());
        assertEquals(Task.Status.COMPLETED, task.getStatus());
        assertEquals(mockUser, task.getUser());
    }

    @Test
    void testAllArgsConstructor() {
        Task task = new Task("New Task", "Description", Task.Status.PENDING, mockUser);

        assertEquals("New Task", task.getTitle());
        assertEquals("Description", task.getDescription());
        assertEquals(Task.Status.PENDING, task.getStatus());
        assertEquals(mockUser, task.getUser());
    }

    @Test
    void testEqualsAndHashCode() throws Exception {
        Task task1 = new Task("Task 1", "Desc 1", Task.Status.PENDING, mockUser);
        Task task2 = new Task("Task 1", "Desc 1", Task.Status.PENDING, mockUser);

        setTaskId(task1, 1L);
        setTaskId(task2, 1L);

        assertEquals(task1, task2);
        assertEquals(31, task1.hashCode());
    }

    @Test
    void testPrePersistAndPreUpdate() {
        Task task = new Task("Task", "Desc", Task.Status.PENDING, mockUser);

        assertNull(task.getCreatedAt());
        assertNull(task.getUpdatedAt());

        task.onCreate();
        assertNotNull(task.getCreatedAt());
        assertNotNull(task.getUpdatedAt());

        LocalDateTime createdAtBeforeUpdate = task.getCreatedAt();
        task.onUpdate();
        assertNotNull(task.getUpdatedAt());
        assertTrue(task.getUpdatedAt().isAfter(createdAtBeforeUpdate) || task.getUpdatedAt().isEqual(createdAtBeforeUpdate));
    }

    private void setTaskId(Task task, Long id) {
        try {
            java.lang.reflect.Field field = Task.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(task, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

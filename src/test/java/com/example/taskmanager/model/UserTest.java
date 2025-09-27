package com.example.taskmanager.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private Task mockTask1;
    private Task mockTask2;

    @BeforeEach
    void setup() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testUser");

        mockTask1 = new Task("Task 1", "Desc 1", Task.Status.PENDING, mockUser);
        mockTask2 = new Task("Task 2", "Desc 2", Task.Status.COMPLETED, mockUser);
    }

    @Test
    void testGettersAndSetters() {
        User user = new User();
        user.setId(10L);
        user.setUsername("john");
        user.setPassword("secret");

        assertEquals(10L, user.getId());
        assertEquals("john", user.getUsername());
        assertEquals("secret", user.getPassword());

        Set<Task> tasks = new HashSet<>();
        tasks.add(mockTask1);
        tasks.add(mockTask2);

        user.setTasks(tasks);
        assertEquals(2, user.getTasks().size());
        assertTrue(user.getTasks().contains(mockTask1));
        assertTrue(user.getTasks().contains(mockTask2));
    }

    @Test
    void testTasksCollectionManipulation() {
        User user = new User();
        user.setUsername("alice");

        assertTrue(user.getTasks().isEmpty());

        user.getTasks().add(mockTask1);
        assertEquals(1, user.getTasks().size());
        assertTrue(user.getTasks().contains(mockTask1));

        user.getTasks().add(mockTask2);
        assertEquals(2, user.getTasks().size());

        user.getTasks().remove(mockTask1);
        assertEquals(1, user.getTasks().size());
        assertFalse(user.getTasks().contains(mockTask1));
    }
}

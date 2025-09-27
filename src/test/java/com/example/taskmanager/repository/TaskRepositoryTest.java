package com.example.taskmanager.repository;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testUser");
        user.setPassword("password");
        userRepository.save(user);
    }

    @Test
    void testFindByUser() {
        Task task1 = new Task("Task 1", "Description 1", Task.Status.PENDING, user);
        Task task2 = new Task("Task 2", "Description 2", Task.Status.COMPLETED, user);
        taskRepository.save(task1);
        taskRepository.save(task2);

        List<Task> tasks = taskRepository.findByUser(user);
        assertEquals(2, tasks.size());
        assertTrue(tasks.stream().anyMatch(t -> t.getTitle().equals("Task 1")));
        assertTrue(tasks.stream().anyMatch(t -> t.getTitle().equals("Task 2")));
    }

    @Test
    void testFindByIdAndUser_Found() {
        Task task = new Task("Task 1", "Description 1", Task.Status.PENDING, user);
        taskRepository.save(task);

        Optional<Task> found = taskRepository.findByIdAndUser(task.getId(), user);
        assertTrue(found.isPresent());
        assertEquals("Task 1", found.get().getTitle());
    }

    @Test
    void testFindByIdAndUser_NotFound() {
        Optional<Task> found = taskRepository.findByIdAndUser(999L, user);
        assertFalse(found.isPresent());
    }
}

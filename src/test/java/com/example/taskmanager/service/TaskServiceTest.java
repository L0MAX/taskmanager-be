package com.example.taskmanager.service;

import com.example.taskmanager.exception.ResourceNotFoundException;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.User;
import com.example.taskmanager.payload.request.TaskRequest;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    private TaskRepository taskRepository;
    private UserRepository userRepository;
    private TaskService taskService;

    private User user;

    @BeforeEach
    void setUp() {
        taskRepository = mock(TaskRepository.class);
        userRepository = mock(UserRepository.class);
        taskService = new TaskService(taskRepository, userRepository);

        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
    }

    // Helper method to set private id via reflection
    private void setTaskId(Task task, Long id) {
        try {
            java.lang.reflect.Field field = Task.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(task, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createTask_ShouldSaveTaskWithDefaultStatus() {
        TaskRequest req = new TaskRequest();
        req.setTitle("New Task");
        req.setDescription("Description");

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(taskRepository.saveAndFlush(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task saved = taskService.createTask(req, "testUser");

        assertNotNull(saved);
        assertEquals("New Task", saved.getTitle());
        assertEquals("Description", saved.getDescription());
        assertEquals(Task.Status.PENDING, saved.getStatus());
        assertEquals(user, saved.getUser());

        verify(taskRepository, times(1)).saveAndFlush(any(Task.class));
    }

    @Test
    void createTask_InvalidStatus_ShouldDefaultToPending() {
        TaskRequest req = new TaskRequest();
        req.setTitle("Task");
        req.setStatus("INVALID");

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(taskRepository.saveAndFlush(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task saved = taskService.createTask(req, "testUser");

        assertEquals(Task.Status.PENDING, saved.getStatus());
    }

    @Test
    void getAllTasks_ShouldReturnTasksForUser() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(taskRepository.findByUser(user)).thenReturn(List.of(new Task(), new Task()));

        List<Task> tasks = taskService.getAllTasks("testUser");

        assertEquals(2, tasks.size());
        verify(taskRepository, times(1)).findByUser(user);
    }

    @Test
    void getTaskById_TaskExists_ShouldReturnTask() {
        Task task = new Task();
        setTaskId(task, 10L);
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(taskRepository.findByIdAndUser(10L, user)).thenReturn(Optional.of(task));

        Task found = taskService.getTaskById(10L, "testUser");

        assertEquals(10L, found.getId());
    }

    @Test
    void getTaskById_TaskDoesNotExist_ShouldThrowException() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(taskRepository.findByIdAndUser(99L, user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.getTaskById(99L, "testUser"));
    }

    @Test
    void updateTask_ShouldUpdateFields() {
        Task existing = new Task("Old", "Desc", Task.Status.PENDING, user);
        setTaskId(existing, 1L);
        TaskRequest req = new TaskRequest();
        req.setTitle("Updated");
        req.setDescription("New Desc");
        req.setStatus("COMPLETED");

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(taskRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task updated = taskService.updateTask(1L, req, "testUser");

        assertEquals("Updated", updated.getTitle());
        assertEquals("New Desc", updated.getDescription());
        assertEquals(Task.Status.COMPLETED, updated.getStatus());
    }

    @Test
    void deleteTask_TaskExists_ShouldCallDelete() {
        Task task = new Task("Task", "Desc", Task.Status.PENDING, user);
        setTaskId(task, 1L);

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(taskRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(task));

        taskService.deleteTask(1L, "testUser");

        verify(taskRepository, times(1)).delete(task);
    }

    @Test
    void deleteTask_TaskDoesNotExist_ShouldThrowException() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(taskRepository.findByIdAndUser(1L, user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.deleteTask(1L, "testUser"));
    }
}

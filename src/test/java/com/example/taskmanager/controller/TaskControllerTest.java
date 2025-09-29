package com.example.taskmanager.controller;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.User;
import com.example.taskmanager.payload.request.TaskRequest;
import com.example.taskmanager.payload.response.ApiResponse;
import com.example.taskmanager.payload.response.TaskResponse;
import com.example.taskmanager.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @InjectMocks
    private TaskController taskController;

    @Mock
    private TaskService taskService;

    @Mock
    private Principal principal;

    private User mockUser;

    @BeforeEach
    void setUp() {
        when(principal.getName()).thenReturn("testUser");

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testUser");
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

    private void setTaskTimestamps(Task task, LocalDateTime created, LocalDateTime updated) {
        try {
            java.lang.reflect.Field createdField = Task.class.getDeclaredField("createdAt");
            createdField.setAccessible(true);
            createdField.set(task, created);

            java.lang.reflect.Field updatedField = Task.class.getDeclaredField("updatedAt");
            updatedField.setAccessible(true);
            updatedField.set(task, updated);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createTask_Success() {
        TaskRequest request = new TaskRequest();
        request.setTitle("New Task");
        request.setDescription("Test Description");

        Task task = new Task("New Task", "Test Description", Task.Status.PENDING, mockUser);
        setTaskId(task, 1L);
        setTaskTimestamps(task, LocalDateTime.now(), LocalDateTime.now());

        when(taskService.createTask(any(TaskRequest.class), eq("testUser"))).thenReturn(task);

        TaskResponse response = taskController.create(request, principal).getBody();

        assertNotNull(response);
        assertEquals(task.getId(), response.getId());
        assertEquals(task.getTitle(), response.getTitle());
        assertEquals(task.getDescription(), response.getDescription());
        assertEquals(task.getStatus().name(), response.getStatus());
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());

        verify(taskService, times(1)).createTask(any(TaskRequest.class), eq("testUser"));
    }

    @Test
    void getAllTasks_Success() {
        Task task1 = new Task("Task 1", "Desc 1", Task.Status.PENDING, mockUser);
        setTaskId(task1, 1L);
        setTaskTimestamps(task1, LocalDateTime.now(), LocalDateTime.now());

        Task task2 = new Task("Task 2", "Desc 2", Task.Status.COMPLETED, mockUser);
        setTaskId(task2, 2L);
        setTaskTimestamps(task2, LocalDateTime.now(), LocalDateTime.now());

        when(taskService.getAllTasks("testUser")).thenReturn(Arrays.asList(task1, task2));

        List<TaskResponse> responses = taskController.getAll(principal).getBody();

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("Task 1", responses.get(0).getTitle());
        assertEquals("Task 2", responses.get(1).getTitle());
        verify(taskService, times(1)).getAllTasks("testUser");
    }

    @Test
    void getTaskById_Success() {
        Task task = new Task("Task 1", "Desc 1", Task.Status.PENDING, mockUser);
        setTaskId(task, 1L);
        setTaskTimestamps(task, LocalDateTime.now(), LocalDateTime.now());

        when(taskService.getTaskById(1L, "testUser")).thenReturn(task);

        TaskResponse response = taskController.getById(1L, principal).getBody();

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Task 1", response.getTitle());
        assertEquals("Desc 1", response.getDescription());
        assertEquals(Task.Status.PENDING.name(), response.getStatus());
        verify(taskService, times(1)).getTaskById(1L, "testUser");
    }

    @Test
    void updateTask_Success() {
        TaskRequest request = new TaskRequest();
        request.setTitle("Updated Task");
        request.setDescription("Updated Description");

        Task updatedTask = new Task("Updated Task", "Updated Description", Task.Status.COMPLETED, mockUser);
        setTaskId(updatedTask, 1L);
        setTaskTimestamps(updatedTask, LocalDateTime.now(), LocalDateTime.now());

        when(taskService.updateTask(eq(1L), any(TaskRequest.class), eq("testUser"))).thenReturn(updatedTask);

        TaskResponse response = taskController.update(1L, request, principal).getBody();

        assertNotNull(response);
        assertEquals("Updated Task", response.getTitle());
        assertEquals("Updated Description", response.getDescription());
        assertEquals(Task.Status.COMPLETED.name(), response.getStatus());
        verify(taskService, times(1)).updateTask(eq(1L), any(TaskRequest.class), eq("testUser"));
    }

    @Test
    void deleteTask_Success() {
        doNothing().when(taskService).deleteTask(1L, "testUser");

        ApiResponse response = taskController.delete(1L, principal).getBody();

        assertNotNull(response);
        assertTrue(response.isSuccess());                       
        assertEquals("Task deleted successfully", response.getMessage());
        verify(taskService, times(1)).deleteTask(1L, "testUser");
    }
}

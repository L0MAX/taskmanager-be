package com.example.taskmanager.controller;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.payload.request.TaskRequest;
import com.example.taskmanager.payload.response.TaskResponse;
import com.example.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    private TaskResponse toResponse(Task t) {
        String created = t.getCreatedAt() != null ? t.getCreatedAt().toString() : null;
        String updated = t.getUpdatedAt() != null ? t.getUpdatedAt().toString() : null;
        return new TaskResponse(t.getId(), t.getTitle(), t.getDescription(),
                t.getStatus().name(), created, updated);
    }

    @PostMapping
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskRequest req, Principal principal) {
        logger.info("User '{}' is creating a new task with title '{}'", principal.getName(), req.getTitle());
        Task created = taskService.createTask(req, principal.getName());
        logger.debug("Task created successfully with id {}", created.getId());
        return ResponseEntity.ok(toResponse(created));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAll(Principal principal) {
        logger.info("User '{}' is fetching all tasks", principal.getName());
        List<TaskResponse> list = taskService.getAllTasks(principal.getName())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        logger.debug("User '{}' retrieved {} tasks", principal.getName(), list.size());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getById(@PathVariable Long id, Principal principal) {
        logger.info("User '{}' is fetching task with id {}", principal.getName(), id);
        Task t = taskService.getTaskById(id, principal.getName());
        return ResponseEntity.ok(toResponse(t));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> update(@PathVariable Long id, @Valid @RequestBody TaskRequest req, Principal principal) {
        logger.info("User '{}' is updating task id {}", principal.getName(), id);
        Task updated = taskService.updateTask(id, req, principal.getName());
        logger.debug("Task id {} updated successfully", id);
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id, Principal principal) {
        logger.warn("User '{}' is deleting task id {}", principal.getName(), id);
        taskService.deleteTask(id, principal.getName());
        logger.info("Task id {} deleted successfully by user '{}'", id, principal.getName());
        return ResponseEntity.ok("Task deleted successfully");
    }
}

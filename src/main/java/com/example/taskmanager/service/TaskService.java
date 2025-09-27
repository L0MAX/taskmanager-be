package com.example.taskmanager.service;

import com.example.taskmanager.exception.ResourceNotFoundException;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.User;
import com.example.taskmanager.payload.request.TaskRequest;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    public Task createTask(TaskRequest req, String username) {
    User user = findUserByUsername(username);

    Task.Status status = Task.Status.PENDING;
    if (req.getStatus() != null) {
        try {
            status = Task.Status.valueOf(req.getStatus());
        } catch (IllegalArgumentException ex) {
            logger.warn("Invalid status '{}' provided by user '{}', defaulting to PENDING", req.getStatus(), username);
        }
    }

    Task t = new Task(req.getTitle(), req.getDescription(), status, user);

    Task saved = taskRepository.saveAndFlush(t);

    logger.info("Task created by {}: id={}, title='{}'", username, saved.getId(), saved.getTitle());

    return saved;
}


    public List<Task> getAllTasks(String username) {
        User user = findUserByUsername(username);
        List<Task> tasks = taskRepository.findByUser(user);
        logger.debug("User '{}' requested all tasks (count={})", username, tasks.size());
        return tasks;
    }

    public Task getTaskById(Long id, String username) {
        User user = findUserByUsername(username);
        return taskRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> {
                    logger.info("Task id={} not found for user '{}'", id, username);
                    return new ResourceNotFoundException("Task not found with id " + id);
                });
    }

    public Task updateTask(Long id, TaskRequest req, String username) {
        User user = findUserByUsername(username);
        Task task = taskRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> {
                    logger.info("Attempt to update non-existing task id={} by user '{}'", id, username);
                    return new ResourceNotFoundException("Task not found with id " + id);
                });

        if (req.getTitle() != null && !req.getTitle().isBlank()) {
            task.setTitle(req.getTitle());
        }
        if (req.getDescription() != null) {
            task.setDescription(req.getDescription());
        }
        if (req.getStatus() != null) {
            try {
                task.setStatus(Task.Status.valueOf(req.getStatus()));
            } catch (IllegalArgumentException ex) {
                logger.warn("Invalid status '{}' provided in update for task id={} by user '{}'", req.getStatus(), id, username);
            }
        }

        Task updated = taskRepository.save(task);
        logger.info("Task updated by {}: id={}", username, updated.getId());
        return updated;
    }

    public void deleteTask(Long id, String username) {
        User user = findUserByUsername(username);
        Task task = taskRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> {
                    logger.info("Attempt to delete non-existing task id={} by user '{}'", id, username);
                    return new ResourceNotFoundException("Task not found with id " + id);
                });

        taskRepository.delete(task);
        logger.info("Task deleted by {}: id={}", username, id);
    }
}

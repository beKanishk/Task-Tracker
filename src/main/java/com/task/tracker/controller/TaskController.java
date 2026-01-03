package com.task.tracker.controller;

import com.task.tracker.authentication.service.AuthService;
import com.task.tracker.dto.TaskRequestDTO;
import com.task.tracker.dto.TaskResponseDTO;
import com.task.tracker.dto.UserResponseDTO;
import com.task.tracker.model.Task;
import com.task.tracker.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final AuthService authService;

    private String extractUserId(String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer "))
            throw new RuntimeException("Missing or invalid Authorization header");

        UserResponseDTO user = authService.getUserFromToken(authHeader);
        return user.getId();
    }

    /**
     * Create Task
     */
    @PostMapping
    public Task createTask(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody TaskRequestDTO dto
    ) {
        dto.setUserId(extractUserId(authHeader));
        return taskService.createTask(dto);
    }

    /**
     * Get Task by Id
     */
    @GetMapping("/{taskId}")
    public Task getTaskById(@PathVariable String taskId) {
        return taskService.findById(taskId);
    }

    /**
     * Get all tasks for logged-in user
     */
    @GetMapping
    public List<Task> getMyTasks(
            @RequestHeader("Authorization") String authHeader
    ) {
        return taskService.findByUserId(extractUserId(authHeader));
    }

    /**
     * Edit Task
     */
    @PutMapping
    public TaskResponseDTO editTask(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody TaskRequestDTO dto
    ) {
        dto.setUserId(extractUserId(authHeader));
        return taskService.editTask(dto);
    }

    /**
     * Delete Task
     */
    @DeleteMapping("/{taskId}")
    public void deleteTask(@PathVariable String taskId) {
        taskService.deleteTask(taskId);
    }
}

package com.task.tracker.controller;

import com.task.tracker.authentication.service.AuthHelper;
import com.task.tracker.dto.TaskRequestDTO;
import com.task.tracker.dto.TaskResponseDTO;
import com.task.tracker.model.Task;
import com.task.tracker.service.TaskService;
import com.task.tracker.utils.TaskActionException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final AuthHelper authHelper;

    @PostMapping
    public TaskResponseDTO createTask(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody TaskRequestDTO dto
    ) {
        dto.setUserId(authHelper.extractUserId(authHeader));
        return taskService.createTask(dto);
    }

    @GetMapping("/{taskId}")
    public Task getTaskById(@PathVariable String taskId) {
        return taskService.findById(taskId);
    }

    @GetMapping
    public List<Task> getMyTasks(
            @RequestHeader("Authorization") String authHeader
    ) {
        return taskService.findByUserId(authHelper.extractUserId(authHeader));
    }

    @PutMapping
    public TaskResponseDTO editTask(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody TaskRequestDTO dto
    ) {
        dto.setUserId(authHelper.extractUserId(authHeader));
        return taskService.editTask(dto);
    }

    @DeleteMapping("/{taskId}")
    public void deleteTask(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String taskId
    ) {
        String userId = authHelper.extractUserId(authHeader);
        Task task = taskService.findById(taskId);
        if (!task.getUserId().equals(userId))
            throw new TaskActionException("Not authorized to delete this task");
        taskService.deleteTask(taskId);
    }
}

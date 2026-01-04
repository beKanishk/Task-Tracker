package com.task.tracker.controller;

import com.task.tracker.authentication.service.AuthService;
import com.task.tracker.dto.TaskStateResponse;
import com.task.tracker.model.Task;
import com.task.tracker.model.TaskStatus;
import com.task.tracker.service.TaskStateService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/tasks/state")
@RequiredArgsConstructor
public class TaskStateController {

    private final TaskStateService taskStateService;
    private final AuthService authService;

    private String extractUserId(String token) {
        return authService.getUserFromToken(token).getId();
    }

    /**
     * Get task status summary
     */
    @GetMapping("/today")
    public TaskStateResponse getTodayState(
            @RequestHeader("Authorization") String token
    ) {
        return taskStateService.getUserTaskState(
                extractUserId(token),
                LocalDate.now()
        );
    }

    @GetMapping("/day")
    public TaskStateResponse getStateForDay(
            @RequestHeader("Authorization") String token,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return taskStateService.getUserTaskState(
                extractUserId(token),
                date
        );
    }

    /**
     * Update task status
     */
    @PutMapping("/{taskId}/status")
    public Task updateTaskStatus(
            @PathVariable String taskId,
            @RequestParam TaskStatus status
    ) {
        return taskStateService.updateTaskStatus(taskId, status);
    }
}


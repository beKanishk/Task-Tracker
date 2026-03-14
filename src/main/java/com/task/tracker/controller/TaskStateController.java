package com.task.tracker.controller;

import com.task.tracker.authentication.service.AuthHelper;
import com.task.tracker.dto.TaskStateResponse;
import com.task.tracker.model.Task;
import com.task.tracker.model.TaskStatus;
import com.task.tracker.service.TaskService;
import com.task.tracker.service.TaskStateService;
import com.task.tracker.utils.TaskActionException;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/tasks/state")
@RequiredArgsConstructor
public class TaskStateController {

    private final TaskStateService taskStateService;
    private final TaskService taskService;
    private final AuthHelper authHelper;

    @GetMapping("/today")
    public TaskStateResponse getTodayState(
            @RequestHeader("Authorization") String token
    ) {
        return taskStateService.getUserTaskState(
                authHelper.extractUserId(token),
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
                authHelper.extractUserId(token),
                date
        );
    }

    @PutMapping("/{taskId}/status")
    public Task updateTaskStatus(
            @RequestHeader("Authorization") String token,
            @PathVariable String taskId,
            @RequestParam TaskStatus status
    ) {
        String userId = authHelper.extractUserId(token);
        Task task = taskService.findById(taskId);
        if (!task.getUserId().equals(userId))
            throw new TaskActionException("Not authorized to update this task");
        return taskStateService.updateTaskStatus(taskId, status);
    }
}

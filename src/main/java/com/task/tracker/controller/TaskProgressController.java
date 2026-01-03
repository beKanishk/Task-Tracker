package com.task.tracker.controller;

import com.task.tracker.authentication.service.AuthService;
import com.task.tracker.dto.TaskProgressRequestDTO;
import com.task.tracker.dto.TaskProgressResponseDTO;
import com.task.tracker.dto.UserResponseDTO;
import com.task.tracker.model.TaskProgress;
import com.task.tracker.service.TaskProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class TaskProgressController {

    private final TaskProgressService taskProgressService;
    private final AuthService authService;

    private String extractUserId(String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer "))
            throw new RuntimeException("Missing or invalid Authorization header");

        UserResponseDTO user = authService.getUserFromToken(authHeader);
        return user.getId();
    }

    /**
     * Mark BOOLEAN task (tick / untick)
     */
    @PostMapping("/boolean/mark")
    public TaskProgressResponseDTO markBooleanTask(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody TaskProgressRequestDTO request
    ) {
        request.setUserId(extractUserId(authHeader));
        TaskProgress progress = taskProgressService.markBooleanTask(request);

        return TaskProgressResponseDTO.builder()
                .taskId(progress.getTaskId())
                .userId(progress.getUserId())
                .date(progress.getDate())
                .completedToday(progress.getCompletedToday())
                .progressPercent(progress.getProgressPercent())
                .taskType("BOOLEAN")
                .completionType("TICK_ONLY")
                .build();
    }

    /**
     * Log quantitative progress
     */
    @PostMapping("/log")
    public TaskProgressResponseDTO logQuantitativeProgress(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody TaskProgressRequestDTO request
    ) {
        request.setUserId(extractUserId(authHeader));
        return taskProgressService.logProgress(request);
    }

    /**
     * Toggle task completion today
     */
    @PostMapping("/toggle-today")
    public TaskProgress toggleToday(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody TaskProgressRequestDTO requestDTO
    ) {
        requestDTO.setUserId(extractUserId(authHeader));
        return taskProgressService.toggleToday(requestDTO);
    }

    /**
     * Get history for a specific task
     */
    @GetMapping("/task/{taskId}/history")
    public List<TaskProgress> getTaskHistory(@PathVariable String taskId) {
        return taskProgressService.getTaskHistory(taskId);
    }

    /**
     * All progress entries for today
     */
    @GetMapping("/day")
    public List<TaskProgress> getUserDayEntries(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return taskProgressService.getUserDayEntries(
                extractUserId(authHeader),
                date
        );
    }

    /**
     * Mark ALL tasks completed today
     */
    @PostMapping("/complete-all-today")
    public List<TaskProgress> markAllTasksCompletedToday(
            @RequestHeader("Authorization") String authHeader
    ) {
        return taskProgressService.markAllTasksCompletedToday(
                extractUserId(authHeader)
        );
    }
}

package com.task.tracker.controller;

import com.task.tracker.authentication.service.AuthHelper;
import com.task.tracker.dto.TaskProgressRequestDTO;
import com.task.tracker.dto.TaskProgressResponseDTO;
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
    private final AuthHelper authHelper;

    /**
     * Mark BOOLEAN task (tick / untick)
     */
    @PostMapping("/boolean/mark")
    public TaskProgressResponseDTO markBooleanTask(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody TaskProgressRequestDTO request
    ) {
        request.setUserId(authHelper.extractUserId(authHeader));
        TaskProgress progress = taskProgressService.markBooleanTask(request);

        // untick path — progress record was deleted
        if (progress == null) {
            return TaskProgressResponseDTO.builder()
                    .taskId(request.getTaskId())
                    .userId(request.getUserId())
                    .date(LocalDate.now())
                    .completedToday(false)
                    .progressPercent(null)
                    .taskType("BOOLEAN")
                    .completionType("TICK_ONLY")
                    .build();
        }

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
        request.setUserId(authHelper.extractUserId(authHeader));
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
        requestDTO.setUserId(authHelper.extractUserId(authHeader));
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
     * All progress entries for a given date
     */
    @GetMapping("/day")
    public List<TaskProgress> getUserDayEntries(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return taskProgressService.getUserDayEntries(
                authHelper.extractUserId(authHeader),
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
                authHelper.extractUserId(authHeader)
        );
    }
}

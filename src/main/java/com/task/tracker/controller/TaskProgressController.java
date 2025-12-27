package com.task.tracker.controller;

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

    /**
     * Mark a single task as completed for today
     */
    @PostMapping("/{userId}/{taskId}/complete-today")
    public TaskProgress markCompletedToday(
            @PathVariable String userId,
            @PathVariable String taskId
    ) {
        return taskProgressService.markCompletedToday(userId, taskId);
    }

    /**
     * Toggle completion for today (mark / unmark)
     */
    @PostMapping("/{userId}/{taskId}/toggle-today")
    public TaskProgress toggleToday(
            @PathVariable String userId,
            @PathVariable String taskId
    ) {
        return taskProgressService.toggleToday(userId, taskId);
    }

    /**
     * Get progress history for a specific task
     */
    @GetMapping("/task/{taskId}/history")
    public List<TaskProgress> getTaskHistory(
            @PathVariable String taskId
    ) {
        return taskProgressService.getTaskHistory(taskId);
    }

    /**
     * Get all progress entries of a user for a specific date
     * (useful for heatmap / LC-style daily view)
     */
    @GetMapping("/{userId}/day")
    public List<TaskProgress> getUserDayEntries(
            @PathVariable String userId,
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return taskProgressService.getUserDayEntries(userId, date);
    }

    /**
     * Mark ALL user tasks completed for today
     */
    @PostMapping("/{userId}/complete-all-today")
    public List<TaskProgress> markAllTasksCompletedToday(
            @PathVariable String userId
    ) {
        return taskProgressService.markAllTasksCompletedToday(userId);
    }
}

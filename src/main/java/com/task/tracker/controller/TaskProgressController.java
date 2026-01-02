package com.task.tracker.controller;

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

    @PostMapping("/boolean/mark")
    public TaskProgressResponseDTO markBooleanTask(
            @RequestBody TaskProgressRequestDTO request
    ) {
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


//    /**
//     * Toggle completion for today (mark / unmark)
//     */
//    @PostMapping("/{userId}/{taskId}/toggle-today")
//    public TaskProgress toggleToday(
//            @PathVariable String userId,
//            @PathVariable String taskId,
//            @RequestParam Integer completed
//    ) {
//        return taskProgressService.toggleToday(userId, taskId);
//    }

    /**
     * Toggle completion for today (mark / unmark)
     */
    @PostMapping("/toggle-today")
    public TaskProgress toggleToday(
            @RequestBody TaskProgressRequestDTO requestDTO
            ) {
        return taskProgressService.toggleToday(requestDTO);
    }

    @PostMapping("/log")
    public TaskProgressResponseDTO logQuantitativeProgress(
            @RequestBody TaskProgressRequestDTO request
    ) {
        return taskProgressService.logProgress(request);

//        return TaskProgressResponseDTO.builder()
//                .taskId(progress.getTaskId())
//                .userId(progress.getUserId())
//                .date(progress.getDate())
//                .completedToday(progress.getCompletedToday())
//                .progressPercent(progress.getProgressPercent())
//                .valueCompleted(progress.getValueCompleted())
//                .completionType(
//                        progress.getValueCompleted() == null
//                                ? "TICK_ONLY"
//                                : "VALUE_LOGGED"
//                )
//                .build();
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

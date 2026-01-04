package com.task.tracker.service;

import com.task.tracker.dto.TaskStateResponse;
import com.task.tracker.model.Task;
import com.task.tracker.model.TaskProgress;
import com.task.tracker.model.TaskStatus;
import com.task.tracker.repository.TaskProgressRepository;
import com.task.tracker.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskStateService {

    private final TaskRepository taskRepository;
    private final TaskProgressRepository taskProgressRepository;

    /**
     * Get task state summary for today
     */
    public TaskStateResponse getUserTaskState(String userId, LocalDate date) {

        List<Task> allTasks = taskRepository.findByUserId(userId);

        List<TaskProgress> todayEntries =
                taskProgressRepository.findByUserIdAndDate(userId, date);

        // tasks completed today
        var completedToday = todayEntries.stream()
                .filter(p -> Boolean.TRUE.equals(p.getCompletedToday()))
                .map(TaskProgress::getTaskId)
                .toList();

        List<Task> tasksCompletedToday = allTasks.stream()
                .filter(t -> completedToday.contains(t.getId()))
                .toList();

        // tasks in progress today
        List<Task> tasksInProgressToday = allTasks.stream()
                .filter(t -> !completedToday.contains(t.getId()))
                .filter(t -> t.getStatus() == TaskStatus.ACTIVE)
                .toList();

        // overall status buckets
        List<Task> tasksCompletedOverall = allTasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.COMPLETED)
                .toList();

        List<Task> tasksActive = allTasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.ACTIVE)
                .toList();

        List<Task> tasksPaused = allTasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.PAUSED)
                .toList();

        return TaskStateResponse.builder()
                .date(date)
                .completedToday(tasksCompletedToday)
                .inProgressToday(tasksInProgressToday)
                .completedOverall(tasksCompletedOverall)
                .active(tasksActive)
                .paused(tasksPaused)
                .build();
    }

    /**
     * Change task status
     */
    public Task updateTaskStatus(String taskId, TaskStatus newStatus) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setStatus(newStatus);

//        // safety rule: reset progress if paused
//        if (newStatus == TaskStatus.PAUSED) {
//            task.setProgressPercent(null);
//        }

        return taskRepository.save(task);
    }
}


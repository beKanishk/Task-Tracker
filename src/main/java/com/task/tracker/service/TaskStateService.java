package com.task.tracker.service;

import com.task.tracker.dto.TaskStateResponse;
import com.task.tracker.dto.TaskTodayDTO;
import com.task.tracker.model.Task;
import com.task.tracker.model.TaskProgress;
import com.task.tracker.model.TaskStatus;
import com.task.tracker.repository.TaskProgressRepository;
import com.task.tracker.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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

        // Map: taskId -> TaskProgress (today)
//        Map<String, TaskProgress> progressMap = todayEntries.stream()
//                .collect(Collectors.toMap(
//                        TaskProgress::getTaskId,
//                        p -> p
//                ));

        Map<String, TaskProgress> progressMap =
                todayEntries.stream()
                        .collect(Collectors.groupingBy(
                                TaskProgress::getTaskId,
                                Collectors.collectingAndThen(
                                        Collectors.maxBy(Comparator.comparing(TaskProgress::getId)),
                                        Optional::get
                                )
                        ));


        List<TaskTodayDTO> completedToday = new ArrayList<>();
        List<TaskTodayDTO> inProgressToday = new ArrayList<>();

        for (Task task : allTasks) {

            if (task.getStatus() != TaskStatus.ACTIVE) continue;

            TaskProgress progress = progressMap.get(task.getId());

            boolean isCompletedToday =
                    progress != null && Boolean.TRUE.equals(progress.getCompletedToday());

            Integer progressPercent =
                    progress != null ? progress.getProgressPercent() : null;

            TaskTodayDTO dto = TaskTodayDTO.builder()
                    .id(task.getId())
                    .title(task.getTitle())
                    .taskType(task.getTaskType())
                    .progressPercent(progressPercent)
                    .completedToday(isCompletedToday)
                    .description(task.getDescription())
                    .target(task.getTargetValue())
                    .unit(task.getUnit())
                    .build();

            if (isCompletedToday) {
                completedToday.add(dto);
            } else {
                inProgressToday.add(dto);
            }
        }

        return TaskStateResponse.builder()
                .inProgressToday(inProgressToday)
                .completedToday(completedToday)
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


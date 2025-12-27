package com.task.tracker.service;

import com.task.tracker.model.Task;
import com.task.tracker.model.TaskProgress;
import com.task.tracker.repository.TaskProgressRepository;
import com.task.tracker.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class TaskProgressService {
    @Autowired
    private TaskProgressRepository taskProgressRepository;

    @Autowired
    private TaskRepository taskRepository;

    public TaskProgress markCompletedToday(String userId, String taskId){
        LocalDate today = LocalDate.now();

        TaskProgress progress = taskProgressRepository.findByUserIdAndDateAndTaskId(userId,today, taskId);

        if (Objects.isNull(progress)) {
            progress = TaskProgress.builder()
                    .userId(userId)
                    .taskId(taskId)
                    .date(today)
                    .completedToday(true)
                    .build();
        } else {
            progress.setCompletedToday(true);
        }

        return taskProgressRepository.save(progress);
    }

    public List<TaskProgress> getTaskHistory(String taskId) {
        return taskProgressRepository.findByTaskId(taskId);
    }

    public List<TaskProgress> getUserDayEntries(String userId, LocalDate date) {
        return taskProgressRepository.findByUserIdAndDate(userId, date);
    }

    public TaskProgress toggleToday(String userId, String taskId) {

        LocalDate today = LocalDate.now();

        TaskProgress existing =
                taskProgressRepository.findByUserIdAndDateAndTaskId(userId,today, taskId);

        if (Objects.isNull(existing)) {
            TaskProgress progress = TaskProgress.builder()
                    .userId(userId)
                    .taskId(taskId)
                    .date(today)
                    .completedToday(true)
                    .build();

            return taskProgressRepository.save(progress);
        }

        taskProgressRepository.delete(existing);
        return null;
    }

    public List<TaskProgress> markAllTasksCompletedToday(String userId) {
        LocalDate today = LocalDate.now();

        // Get all tasks for user
        List<Task> tasks = taskRepository.findByUserId(userId);
        List<TaskProgress> results = new ArrayList<>();

        for (Task task : tasks) {
            TaskProgress existing =
                    taskProgressRepository.findByUserIdAndDateAndTaskId(userId,today, task.getId());

            if (existing == null) {
                // create new entry
                TaskProgress progress = TaskProgress.builder()
                        .userId(userId)
                        .taskId(task.getId())
                        .date(today)
                        .completedToday(true)
                        .build();

                results.add(taskProgressRepository.save(progress));
            } else {
                // already marked — just return existing
                results.add(existing);
            }
        }

        return results;
    }

}

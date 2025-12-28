package com.task.tracker.service;

import com.task.tracker.dto.TaskProgressRequestDTO;
import com.task.tracker.model.Task;
import com.task.tracker.model.TaskProgress;
import com.task.tracker.model.TaskStatus;
import com.task.tracker.model.TaskType;
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

    @Autowired
    private DailySummaryService dailySummaryService;

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

    public TaskProgress toggleToday(TaskProgressRequestDTO requestDto) {
        String userId = requestDto.getUserId();
        String taskId = requestDto.getTaskId();
        
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

    public TaskProgress logProgress(TaskProgressRequestDTO requestDTO) {

        String taskId = requestDTO.getTaskId();
        String userId = requestDTO.getUserId();
        Integer valueCompleted = requestDTO.getValueCompleted();

        LocalDate today = LocalDate.now();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        TaskProgress progress =
                taskProgressRepository.findByUserIdAndDateAndTaskId(userId, today, taskId);

        if (progress == null) {
            progress = TaskProgress.builder()
                    .userId(userId)
                    .taskId(taskId)
                    .date(today)
                    .build();
        }

        // QUANTITATIVE TASK
        if (task.getTaskType() == TaskType.QUANTITATIVE) {

            // no value entered → treat as tick done
            if (valueCompleted == null) {

                progress.setValueCompleted(null);
                progress.setCompletedToday(true);
                progress.setProgressPercent(100);

                // don't modify task progress snapshot
                return taskProgressRepository.save(progress);
            }

            // avoid invalid or negative input
            if (valueCompleted < 0) valueCompleted = 0;

            progress.setValueCompleted(valueCompleted);

            Integer target = task.getTargetValue();

            // avoid divide-by-zero
            if (target == null || target == 0) {
                progress.setProgressPercent(null);
                progress.setCompletedToday(true);
                return taskProgressRepository.save(progress);
            }


            int percent = (int) Math.round(
                    (valueCompleted * 100.0) / target
            );

            if (percent > 100) percent = 100;
            if (percent < 0) percent = 0;

            progress.setProgressPercent(percent);
            progress.setCompletedToday(percent > 0);

            // update task snapshot
            task.setProgressPercent(percent);

            if (percent == 100) {
                task.setStatus(TaskStatus.COMPLETED);
            }

            taskRepository.save(task);
        }

        // BOOLEAN TASK
        else if (task.getTaskType() == TaskType.BOOLEAN) {
            progress.setCompletedToday(true);

            // do NOT force progressPercent if task has no measurable value
            progress.setProgressPercent(null);
        }

        TaskProgress saved = taskProgressRepository.save(progress);
        dailySummaryService.recomputeSummaryForToday(userId);

        return saved;
    }

    public TaskProgress markBooleanTask(TaskProgressRequestDTO request) {

        String userId = request.getUserId();
        String taskId = request.getTaskId();
        Boolean completed = request.getCompleted();

        LocalDate today = LocalDate.now();

        TaskProgress progress =
                taskProgressRepository.findByUserIdAndDateAndTaskId(userId, today, taskId);

        // unmark case
        if (Boolean.FALSE.equals(completed) && progress != null) {
            taskProgressRepository.delete(progress);
            dailySummaryService.recomputeSummaryForToday(userId);
            return null;
        }

        // mark done
        if (progress == null) {
            progress = TaskProgress.builder()
                    .userId(userId)
                    .taskId(taskId)
                    .date(today)
                    .build();
        }

        progress.setCompletedToday(true);
        progress.setProgressPercent(null);

        TaskProgress saved = taskProgressRepository.save(progress);
        dailySummaryService.recomputeSummaryForToday(userId);

        return saved;
    }


}

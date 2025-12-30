package com.task.tracker.service;

import com.task.tracker.dto.TaskRequestDTO;
import com.task.tracker.dto.TaskResponseDTO;
import com.task.tracker.model.Task;
import com.task.tracker.model.TaskStatus;
import com.task.tracker.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public List<Task> findByUserId(String userId) {
        return taskRepository.findByUserId(userId);
    }

    public Task findById(String id) {
        Optional<Task> task = taskRepository.findById(id);

        if(task.isPresent()) {
            return task.get();
        }else {
            throw new RuntimeException("Task Not Found");
        }
    }

    public Task createTask(TaskRequestDTO dto) {
        Task task = Task.builder()
                .userId(dto.getUserId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .targetValue(dto.getTargetValue())
                .status(TaskStatus.ACTIVE)   // default
                .createdAt(LocalDate.now())
                .build();

        return taskRepository.save(task);
    }

    public List<String> findDistinctUserIds() {
        return taskRepository.findAllUserIdOnly()
                .stream()
                .map(Task::getUserId)
                .distinct()
                .toList();
    }

    public TaskResponseDTO editTask(TaskRequestDTO dto) {

        Task task = taskRepository.findById(dto.getTaskId())
                .orElseThrow(() -> new RuntimeException("Task Not Found"));

        if (dto.getTitle() != null)
            task.setTitle(dto.getTitle());

        if (dto.getDescription() != null)
            task.setDescription(dto.getDescription());

        if (dto.getStartDate() != null)
            task.setStartDate(dto.getStartDate());

        if (dto.getEndDate() != null)
            task.setEndDate(dto.getEndDate());

        if (dto.getTargetValue() != null)
            task.setTargetValue(dto.getTargetValue());

        if (dto.getUnit() != null)
            task.setUnit(dto.getUnit());

        if (dto.getTaskType() != null)
            task.setTaskType(dto.getTaskType());

        Task editedTask = taskRepository.save(task);

        return TaskResponseDTO.builder()
                .userId(editedTask.getUserId())
                .title(editedTask.getTitle())
                .description(editedTask.getDescription())
                .status(editedTask.getStatus())
                .taskType(editedTask.getTaskType())
                .targetValue(editedTask.getTargetValue())
                .unit(editedTask.getUnit())
                .startDate(editedTask.getStartDate())
                .endDate(editedTask.getEndDate())
                .build();
    }

    public void deleteTask(String id) {
        taskRepository.deleteById(id);
    }
}

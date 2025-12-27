package com.task.tracker.service;

import com.task.tracker.dto.TaskRequestDTO;
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
                .targetHours(dto.getTargetHours())
                .status(TaskStatus.ACTIVE)   // default
                .createdAt(LocalDate.now())
                .build();

        return taskRepository.save(task);
    }

}

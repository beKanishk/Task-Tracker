package com.task.tracker.controller;

import com.task.tracker.dto.TaskRequestDTO;
import com.task.tracker.dto.TaskResponseDTO;
import com.task.tracker.model.Task;
import com.task.tracker.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    //get task which are in progress and task which are completed for today or overall and
    // to change the status of task from ACTIVE to completed or paused

    /**
     * Create Task
     */
    @PostMapping
    public Task createTask(@RequestBody TaskRequestDTO dto) {
        return taskService.createTask(dto);
    }

    /**
     * Get Task by Id
     */
    @GetMapping("/{taskId}")
    public Task getTaskById(@PathVariable String taskId) {
        return taskService.findById(taskId);
    }

    /**
     * Get all tasks of a user
     */
    @GetMapping("/user/{userId}")
    public List<Task> getTasksByUser(@PathVariable String userId) {
        return taskService.findByUserId(userId);
    }

    /**
     * Edit / Update Task
     */
    @PutMapping
    public TaskResponseDTO editTask(@RequestBody TaskRequestDTO dto) {
        return taskService.editTask(dto);
    }

    /**
     * Delete Task
     */
    @DeleteMapping("/{taskId}")
    public void deleteTask(@PathVariable String taskId) {
        taskService.deleteTask(taskId);
    }

    /**
     * Get unique users who have tasks
     */
    @GetMapping("/users")
    public List<String> getAllUserIds() {
        return taskService.findDistinctUserIds();
    }
}

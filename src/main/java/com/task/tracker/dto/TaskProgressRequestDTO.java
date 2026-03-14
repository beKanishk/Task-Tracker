package com.task.tracker.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskProgressRequestDTO {
    private String userId;

    @NotBlank(message = "Task ID is required")
    private String taskId;

    @Min(value = 0, message = "Value completed cannot be negative")
    private Integer valueCompleted;

    @NotNull(message = "Completed flag is required")
    private Boolean completed;
}

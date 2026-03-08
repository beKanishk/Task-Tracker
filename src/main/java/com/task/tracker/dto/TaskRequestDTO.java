package com.task.tracker.dto;

import com.task.tracker.model.TaskType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskRequestDTO {
    private String taskId;
    private String taskName;
    private String userId;

    @NotBlank(message = "Title is required")
    private String title;
    private String description;

    private LocalDate startDate;
    private LocalDate endDate;

    private Integer targetValue;
    private String unit;

    @NotNull(message = "Task type is required")
    private TaskType taskType;
}

package com.task.tracker.dto;

import com.task.tracker.model.TaskType;
import jakarta.annotation.Nullable;
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

    private String title;
    private String description;

    private LocalDate startDate;
    private LocalDate endDate;

    private Integer targetValue;
    private String unit;

    private TaskType taskType;
}

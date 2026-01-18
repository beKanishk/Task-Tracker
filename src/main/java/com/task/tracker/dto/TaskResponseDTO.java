package com.task.tracker.dto;

import com.task.tracker.model.TaskStatus;
import com.task.tracker.model.TaskType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class TaskResponseDTO {
    private String userId;

    private String title;
    private String description;

    private TaskStatus status;

    private TaskType taskType;
    private boolean completedToday;

    private Integer targetValue;
    private String unit;
    private LocalDate startDate;
    private LocalDate endDate;
}

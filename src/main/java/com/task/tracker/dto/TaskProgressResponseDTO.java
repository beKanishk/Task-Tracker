package com.task.tracker.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class TaskProgressResponseDTO {

    private String taskId;
    private String userId;

    private LocalDate date;

    private Boolean completedToday;
    private Integer progressPercent;
    private Integer valueCompleted;

    // Useful for frontend logic
    private String taskType;         // BOOLEAN | QUANTITATIVE
    private String completionType;   // VALUE_LOGGED | TICK_ONLY
}

package com.task.tracker.dto;

import lombok.Data;

@Data
public class TaskProgressRequestDTO {
    private String userId;
    private String taskId;
    private Integer valueCompleted;
    private Boolean completed;
}

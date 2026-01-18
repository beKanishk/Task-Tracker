package com.task.tracker.dto;

import com.task.tracker.model.TaskType;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TaskTodayDTO {
    private String id;
    private String title;
    private TaskType taskType;
    private Integer progressPercent;
    private String description;
    private Integer target;
    private String unit;
    private boolean completedToday;
}

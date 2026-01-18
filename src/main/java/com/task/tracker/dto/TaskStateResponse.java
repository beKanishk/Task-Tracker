package com.task.tracker.dto;

import com.task.tracker.model.Task;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class TaskStateResponse {

    private LocalDate date;

    private List<TaskTodayDTO> completedToday;
    private List<TaskTodayDTO> inProgressToday;

    private List<Task> completedOverall;
    private List<Task> active;
    private List<Task> paused;
}


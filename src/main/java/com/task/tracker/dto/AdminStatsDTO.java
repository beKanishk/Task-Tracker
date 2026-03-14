package com.task.tracker.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class AdminStatsDTO {
    private long totalUsers;
    private long newUsersToday;
    private long loggedInToday;
    private long activeToday;
    private long activeThisWeek;
    private long totalTasks;
    private long tasksCreatedToday;
    private Map<String, Long> feedbackByType;
}

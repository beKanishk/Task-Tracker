package com.task.tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyStatsDTO {
    private int year;
    private int month;
    private int totalTasksCompleted;
    private int avgCompletionPercent;
    private int activeDays;
    private int totalDays;
    private LocalDate bestDay;
    private int bestDayCompleted;
}

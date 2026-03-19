package com.task.tracker.service;

import com.task.tracker.dto.MonthlyStatsDTO;
import com.task.tracker.model.DailySummary;
import com.task.tracker.model.TaskProgress;
import com.task.tracker.repository.DailySummaryRepository;
import com.task.tracker.repository.TaskProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class DailySummaryService {

    @Autowired
    private TaskProgressRepository taskProgressRepository;

    @Autowired
    private DailySummaryRepository dailySummaryRepository;

    public void recomputeSummaryForToday(String userId) {
        computeDailySummary(userId, LocalDate.now());
    }


    @Transactional
    public DailySummary computeDailySummary(String userId, LocalDate date) {

        List<TaskProgress> entries =
                taskProgressRepository.findByUserIdAndDate(userId, date);

        if (entries.isEmpty()) {
            return DailySummary.builder()
                    .userId(userId)
                    .date(date)
                    .totalProgressPercent(0)
                    .tasksCompleted(0)
                    .tasksInProgress(0)
                    .build();
        }

        int totalPercent = 0;
        int counted = 0;

        int completed = 0;
        int inProgress = 0;

        for (TaskProgress e : entries) {

            Integer percent = e.getProgressPercent();

            // BOOLEAN task -> 100% if completed, 0% if not
            if (percent == null) {
                if (Boolean.TRUE.equals(e.getCompletedToday())) {
                    completed++;
                    totalPercent += 100;
                }
                counted++;
                continue;
            }

            // QUANTITATIVE -> contributes to avg
            totalPercent += percent;
            counted++;

            if (Boolean.TRUE.equals(e.getCompletedToday())) {
                if (percent == 100)
                    completed++;
                else
                    inProgress++;
            }
        }

        int avgPercent = counted == 0 ? 0 : (totalPercent / counted);

        DailySummary summary = DailySummary.builder()
                .userId(userId)
                .date(date)
                .totalProgressPercent(avgPercent)
                .tasksCompleted(completed)
                .tasksInProgress(inProgress)
                .build();

        return dailySummaryRepository.save(summary);
    }



    public MonthlyStatsDTO getMonthlyStats(String userId, int year, int month) {
        List<DailySummary> summaries = getMonthlySummaries(userId, year, month);
        int totalDays = LocalDate.of(year, month, 1).lengthOfMonth();

        if (summaries.isEmpty()) {
            return MonthlyStatsDTO.builder()
                    .year(year).month(month)
                    .totalTasksCompleted(0).avgCompletionPercent(0)
                    .activeDays(0).totalDays(totalDays)
                    .build();
        }

        int totalCompleted = summaries.stream().mapToInt(DailySummary::getTasksCompleted).sum();
        int avgPercent = (int) summaries.stream()
                .mapToInt(s -> s.getTotalProgressPercent() != null ? s.getTotalProgressPercent() : 0)
                .average().orElse(0);
        DailySummary best = summaries.stream()
                .max(Comparator.comparingInt(s -> Objects.requireNonNullElse(s.getTasksCompleted(), 0) + Objects.requireNonNullElse(s.getTasksInProgress(), 0))).orElse(null);

        return MonthlyStatsDTO.builder()
                .year(year).month(month)
                .totalTasksCompleted(totalCompleted)
                .avgCompletionPercent(avgPercent)
                .activeDays(summaries.size())
                .totalDays(totalDays)
                .bestDay(best != null ? best.getDate() : null)
                .bestDayCompleted(best != null ? Objects.requireNonNullElse(best.getTasksCompleted(), 0) + Objects.requireNonNullElse(best.getTasksInProgress(), 0) : 0)
                .build();
    }

    public List<DailySummary> getWeeklySummaries(String userId) {
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(6);

        List<LocalDate> activeDates = taskProgressRepository
                .findByUserIdAndDateBetween(userId, weekAgo, today)
                .stream()
                .map(TaskProgress::getDate)
                .distinct()
                .sorted()
                .toList();

        List<DailySummary> summaries = new ArrayList<>();
        for (LocalDate date : activeDates) {
            DailySummary summary = computeDailySummary(userId, date);
            if (summary != null) summaries.add(summary);
        }
        return summaries;
    }

    public List<DailySummary> getMonthlySummaries(String userId, int year, int month) {

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1).minusDays(1);

        // fetch only days that actually have activity
        List<LocalDate> activeDates = taskProgressRepository
                .findByUserIdAndDateBetween(userId, start, end)
                .stream()
                .map(TaskProgress::getDate)
                .distinct()
                .sorted()
                .toList();

        List<DailySummary> summaries = new ArrayList<>();

        for (LocalDate date : activeDates) {

            DailySummary summary = computeDailySummary(userId, date);

            if (summary != null) {
                summaries.add(summary);
            }
        }

        return summaries;
    }


}

package com.task.tracker.service;

import com.task.tracker.model.FatigueLevel;
import com.task.tracker.model.Task;
import com.task.tracker.model.TaskProgress;
import com.task.tracker.model.TaskType;
import com.task.tracker.model.UserFatigue;
import com.task.tracker.repository.TaskProgressRepository;
import com.task.tracker.repository.TaskRepository;
import com.task.tracker.repository.UserFatigueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FatigueService {

    private final TaskRepository taskRepo;
    private final TaskProgressRepository progressRepo;
    private final UserFatigueRepository fatigueRepo;

    /**
     * Cached evaluation (SAFE for dashboard)
     */
    public UserFatigue getOrEvaluate(String userId, LocalDate today) {

        return fatigueRepo.findByUserId(userId)
                .filter(f -> f.getEvaluatedOn().equals(today))
                .orElseGet(() -> evaluateFatigue(userId, today));
    }

    /* =====================================================
       PUBLIC API
       ===================================================== */

    public UserFatigue evaluateFatigue(String userId, LocalDate today) {

        LocalDate startDate = today.minusDays(6);

        double completionTrend = calculateCompletionTrend(userId, startDate, today);
        int lowEffortDays = calculateLowEffortDays(userId, startDate, today);
        List<String> avoidedTasks = findAvoidedTasks(userId, startDate, today);

        int score = score(
                completionTrend,
                lowEffortDays,
                avoidedTasks.size()
        );

        FatigueLevel level = mapLevel(score);

        UserFatigue fatigue = UserFatigue.builder()
                .userId(userId)
                .fatigueScore(score)
                .level(level)
                .completionTrend(completionTrend)
                .lowEffortDays(lowEffortDays)
                .avoidedTasks(avoidedTasks)
                .evaluatedOn(today)
                .build();

        return fatigueRepo.save(fatigue);
    }

    /* =====================================================
       SIGNAL 1 — COMPLETION TREND
       ===================================================== */

    /**
     * Measures whether daily completions are declining.
     * Negative slope => fatigue.
     */
    private double calculateCompletionTrend(
            String userId,
            LocalDate start,
            LocalDate end
    ) {
        Map<LocalDate, Long> completedPerDay = progressRepo
                .findByUserIdAndDateBetween(userId, start, end)
                .stream()
                .filter(TaskProgress::getCompletedToday)
                .collect(Collectors.groupingBy(
                        TaskProgress::getDate,
                        Collectors.counting()
                ));

        // Build ordered list (7 days)
        List<Long> series = new ArrayList<>();
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            series.add(completedPerDay.getOrDefault(d, 0L));
        }

        // Simple slope: last - first
        return series.get(series.size() - 1) - series.get(0);
    }

    /* =====================================================
       SIGNAL 2 — LOW EFFORT DAYS
       ===================================================== */

    /**
     * Low effort = day where:
     * - only 1 task logged OR
     * - quantitative task progress < 20%
     */
    private int calculateLowEffortDays(
            String userId,
            LocalDate start,
            LocalDate end
    ) {
        Map<LocalDate, List<TaskProgress>> byDay = progressRepo
                .findByUserIdAndDateBetween(userId, start, end)
                .stream()
                .collect(Collectors.groupingBy(TaskProgress::getDate));

        int count = 0;

        for (var entry : byDay.entrySet()) {
            List<TaskProgress> logs = entry.getValue();

            if (logs.size() == 1) {
                count++;
                continue;
            }

            boolean lowQuant = logs.stream()
                    .filter(p -> p.getProgressPercent() != null)
                    .anyMatch(p -> p.getProgressPercent() < 25);

            if (lowQuant) count++;
        }

        return count;
    }

    /* =====================================================
       SIGNAL 3 — TASK AVOIDANCE
       ===================================================== */

    /**
     * A task is avoided if:
     * - ACTIVE
     * - not logged at all in last 3+ days
     */
    private List<String> findAvoidedTasks(
            String userId,
            LocalDate start,
            LocalDate end
    ) {
        List<Task> activeTasks = taskRepo.findByUserIdAndStatusActive(userId);

        Set<String> loggedTaskIds = progressRepo
                .findByUserIdAndDateBetween(userId, start, end)
                .stream()
                .map(TaskProgress::getTaskId)
                .collect(Collectors.toSet());

        return activeTasks.stream()
                .filter(task -> !loggedTaskIds.contains(task.getId()))
                .map(Task::getTitle)
                .limit(3) // cap for UX sanity
                .toList();
    }

    /* =====================================================
       SCORING
       ===================================================== */

    private int score(
            double completionTrend,
            int lowEffortDays,
            int avoidedCount
    ) {
        int score = 0;

        // Completion trend (max 40)
        if (completionTrend < 0) {
            score += Math.min(40, Math.abs((int) completionTrend) * 10);
        }

        // Low effort (max 20)
        score += Math.min(20, lowEffortDays * 7);

        // Avoidance (max 15)
        score += Math.min(15, avoidedCount * 5);

        return Math.min(score, 100);
    }

    /* =====================================================
       LEVEL MAPPING
       ===================================================== */

    private FatigueLevel mapLevel(int score) {
        if (score < 20) return FatigueLevel.NONE;
        if (score < 40) return FatigueLevel.LOW;
        if (score < 70) return FatigueLevel.MEDIUM;
        return FatigueLevel.HIGH;
    }
}

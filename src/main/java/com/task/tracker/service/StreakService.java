package com.task.tracker.service;

import com.task.tracker.model.UserStreak;
import com.task.tracker.process.StreakProperties;
import com.task.tracker.repository.UserStreakRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class StreakService {

    private final UserStreakRepository repository;
    private final StreakProperties properties;

    /**
     * Call this ONLY when user has at least one completed task today
     */
    public void updateStreak(String userId, LocalDate today) {

        UserStreak streak = repository.findById(userId)
                .orElseGet(() -> UserStreak.builder()
                        .userId(userId)
                        .currentStreak(0)
                        .longestStreak(0)
                        .forgivenessUsed(0)
                        .forgivenessAllowed(properties.getForgivenessAllowed())
                        .build());

        LocalDate lastActive = streak.getLastActiveDate();

        /* ================= FIRST EVER ACTIVITY ================= */
        if (lastActive == null) {
            streak.start(today, properties.getForgivenessAllowed());
            repository.save(streak);
            return;
        }

        long gap = ChronoUnit.DAYS.between(lastActive, today);

        /* ================= SAME DAY ================= */
        if (gap == 0) {
            // already counted today
            return;
        }

        /* ================= CONSECUTIVE DAY ================= */
        if (gap == 1) {
            streak.increment(today);
            repository.save(streak);
            return;
        }

        /* ================= MISSED DAYS ================= */
        int missedDays = (int) gap - 1;

        boolean forgivenessEnabled = properties.isAllowForgiveness();
        boolean withinMaxGap = missedDays <= properties.getMaxGapDays();
        boolean canUseForgiveness = streak.canUseForgiveness(missedDays);

        /*
         * Do NOT auto-consume forgiveness
         * Mark it as PENDING and wait for user decision
         */
        if (forgivenessEnabled && withinMaxGap && canUseForgiveness) {
            streak.markForgivenessPending(missedDays);
        } else {
            streak.reset(today, properties.getForgivenessAllowed());
        }

        repository.save(streak);
    }

    /**
     * User ACCEPTS forgiveness
     */
    public void acceptForgiveness(String userId, LocalDate today) {
        UserStreak streak = repository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("Streak not found"));

        if (!streak.isForgivenessDecisionRequired()) {
            return;
        }

        streak.consumeForgiveness(today);
        repository.save(streak);
    }

    /**
     * User DECLINES forgiveness
     */
    public void declineForgiveness(String userId, LocalDate today) {
        UserStreak streak = repository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("Streak not found"));

        if (!streak.isForgivenessDecisionRequired()) {
            return;
        }

        streak.reset(today, properties.getForgivenessAllowed());
        repository.save(streak);
    }

    /**
     * Read-only API for UI / dashboard
     */
    public UserStreak getStreak(String userId) {
        return repository.findById(userId)
                .orElseGet(() -> UserStreak.builder()
                        .userId(userId)
                        .currentStreak(0)
                        .longestStreak(0)
                        .forgivenessUsed(0)
                        .forgivenessAllowed(properties.getForgivenessAllowed())
                        .build());
    }
}
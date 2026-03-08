package com.task.tracker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final StreakService streakService;

    /**
     * Delegates to StreakService which maintains currentStreak as a persisted field,
     * avoiding the previous N+1 query-per-day loop.
     */
    public int getCurrentStreak(String userId) {
        return streakService.getStreak(userId).getCurrentStreak();
    }
}

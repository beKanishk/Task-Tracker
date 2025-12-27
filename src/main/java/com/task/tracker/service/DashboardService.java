package com.task.tracker.service;

import com.task.tracker.repository.TaskProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TaskProgressRepository progressRepository;

    public int getCurrentStreak(String userId) {

        int streak = 0;
        LocalDate date = LocalDate.now();

        while (true) {
            boolean workedToday =
                    !progressRepository.findByUserIdAndDate(userId, date).isEmpty();

            if (!workedToday) break;

            streak++;
            date = date.minusDays(1);
        }

        return streak;
    }
}

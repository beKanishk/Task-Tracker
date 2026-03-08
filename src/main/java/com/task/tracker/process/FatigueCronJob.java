package com.task.tracker.process;

import com.task.tracker.service.FatigueService;
import com.task.tracker.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class FatigueCronJob {

    private final TaskService taskService;
    private final FatigueService fatigueService;

    /**
     * Runs daily at 2 AM.
     * Uses a userId projection query (not findAll) to avoid loading every User document.
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void evaluateDailyFatigue() {
        LocalDate today = LocalDate.now();
        taskService.findDistinctUserIds()
                .forEach(userId -> fatigueService.evaluateFatigue(userId, today));
    }
}


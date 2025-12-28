package com.task.tracker.process;

import com.task.tracker.service.DailySummaryService;
import com.task.tracker.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DailySummaryCronJob {

    private final TaskService taskService;
    private final DailySummaryService dailySummaryService;

    @Scheduled(cron = "0 59 23 * * *")
    public void generateDailySnapshots() {

        LocalDate today = LocalDate.now();

        // get distinct userIds that have tasks
        List<String> userIds =
                taskService.findDistinctUserIds();

        for (String userId : userIds) {
            dailySummaryService.computeDailySummary(userId, today);
        }
    }
}


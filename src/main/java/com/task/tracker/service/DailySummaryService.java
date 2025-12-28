package com.task.tracker.service;

import com.task.tracker.model.DailySummary;
import com.task.tracker.model.TaskProgress;
import com.task.tracker.repository.DailySummaryRepository;
import com.task.tracker.repository.TaskProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DailySummaryService {

    @Autowired
    private TaskProgressRepository taskProgressRepository;

    @Autowired
    private DailySummaryRepository dailySummaryRepository;

    public void recomputeSummaryForToday(String userId) {
        computeDailySummary(userId, LocalDate.now());
    }


    public DailySummary computeDailySummary(String userId, LocalDate date) {

        List<TaskProgress> entries = taskProgressRepository.findByUserIdAndDate(userId, date);

        if (entries.isEmpty()) {
            return null;
        }

        int totalPercent = 0;
        int counted = 0;

        int completed = 0;
        int inProgress = 0;

        for (TaskProgress e : entries) {

            Integer percent = e.getProgressPercent();

            // BOOLEAN tasks may have null percent — skip from avg
            if (percent != null) {
                totalPercent += percent;
                counted++;
            }

            if (Boolean.TRUE.equals(e.getCompletedToday())) {
                if (percent != null && percent == 100)
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

}

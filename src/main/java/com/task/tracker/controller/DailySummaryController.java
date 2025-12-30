package com.task.tracker.controller;

import com.task.tracker.model.DailySummary;
import com.task.tracker.service.DailySummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/summary")
@RequiredArgsConstructor
public class DailySummaryController {

    private final DailySummaryService dailySummaryService;

    /**
     * Get / Generate summary for a specific date
     */
    @GetMapping("/{userId}")
    public DailySummary getSummaryForDate(
            @PathVariable String userId,
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return dailySummaryService.computeDailySummary(userId, date);
    }

    /**
     * Get today's summary
     */
    @GetMapping("/{userId}/today")
    public DailySummary getTodaySummary(@PathVariable String userId) {
        return dailySummaryService.computeDailySummary(userId, LocalDate.now());
    }

    /**
     * Monthly summary list (for charts / trends / reports)
     */
    @GetMapping("/{userId}/month")
    public List<DailySummary> getMonthlySummary(
            @PathVariable String userId,
            @RequestParam int year,
            @RequestParam int month
    ) {
        return dailySummaryService.getMonthlySummaries(userId, year, month);
    }
}

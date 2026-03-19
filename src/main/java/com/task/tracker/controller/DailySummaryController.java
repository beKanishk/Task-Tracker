package com.task.tracker.controller;

import com.task.tracker.authentication.service.AuthHelper;
import com.task.tracker.dto.MonthlyStatsDTO;
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
    private final AuthHelper authHelper;

    @GetMapping("/day")
    public DailySummary getSummaryForDate(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return dailySummaryService.computeDailySummary(authHelper.extractUserId(authHeader), date);
    }

    @GetMapping("/today")
    public DailySummary getTodaySummary(
            @RequestHeader("Authorization") String authHeader
    ) {
        return dailySummaryService.computeDailySummary(authHelper.extractUserId(authHeader), LocalDate.now());
    }

    @GetMapping("/week")
    public List<DailySummary> getWeeklySummary(
            @RequestHeader("Authorization") String authHeader
    ) {
        return dailySummaryService.getWeeklySummaries(authHelper.extractUserId(authHeader));
    }

    @GetMapping("/month/stats")
    public MonthlyStatsDTO getMonthlyStats(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam int year,
            @RequestParam int month
    ) {
        return dailySummaryService.getMonthlyStats(authHelper.extractUserId(authHeader), year, month);
    }

    @GetMapping("/month")
    public List<DailySummary> getMonthlySummary(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam int year,
            @RequestParam int month
    ) {
        return dailySummaryService.getMonthlySummaries(authHelper.extractUserId(authHeader), year, month);
    }
}

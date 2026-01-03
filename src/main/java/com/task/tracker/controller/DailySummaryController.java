package com.task.tracker.controller;

import com.task.tracker.authentication.service.AuthService;
import com.task.tracker.dto.UserResponseDTO;
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
    private final AuthService authService;

    private String extractUserId(String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        UserResponseDTO user = authService.getUserFromToken(authHeader);
        return user.getId();
    }

    /**
     * Get / Generate summary for a specific date
     */
    @GetMapping("/day")
    public DailySummary getSummaryForDate(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        String userId = extractUserId(authHeader);
        return dailySummaryService.computeDailySummary(userId, date);
    }

    /**
     * Get today's summary
     */
    @GetMapping("/today")
    public DailySummary getTodaySummary(
            @RequestHeader("Authorization") String authHeader
    ) {
        String userId = extractUserId(authHeader);
        return dailySummaryService.computeDailySummary(userId, LocalDate.now());
    }

    /**
     * Monthly summary list (for charts / trends / reports)
     */
    @GetMapping("/month")
    public List<DailySummary> getMonthlySummary(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam int year,
            @RequestParam int month
    ) {
        String userId = extractUserId(authHeader);
        return dailySummaryService.getMonthlySummaries(userId, year, month);
    }
}

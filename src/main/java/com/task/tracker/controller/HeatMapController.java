package com.task.tracker.controller;

import com.task.tracker.authentication.service.AuthService;
import com.task.tracker.dto.UserResponseDTO;
import com.task.tracker.model.Heatmap;
import com.task.tracker.service.HeatMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/heatmap")
@RequiredArgsConstructor
public class HeatMapController {

    private final HeatMapService heatMapService;
    private final AuthService authService;

    private String extractUserId(String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer "))
            throw new RuntimeException("Missing or invalid Authorization header");

        UserResponseDTO user = authService.getUserFromToken(authHeader);
        return user.getId();
    }

    /**
     * Get heatmap for a specific month
     */
    @GetMapping("/month")
    public Heatmap getMonthlyHeatmap(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam int year,
            @RequestParam int month
    ) {
        return heatMapService.getOrCreateMonth(
                extractUserId(authHeader),
                year,
                month
        );
    }

    /**
     * Get heatmap for whole year
     */
    @GetMapping("/year")
    public List<Heatmap> getYearHeatmap(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam int year
    ) {
        return heatMapService.getYearHeatmap(
                extractUserId(authHeader),
                year
        );
    }

    /**
     * Get heatmap month containing this date
     */
    @GetMapping("/day")
    public Heatmap getHeatmapForDay(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return heatMapService.getOrCreateMonth(
                extractUserId(authHeader),
                date.getYear(),
                date.getMonthValue()
        );
    }
}

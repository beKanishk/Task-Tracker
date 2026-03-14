package com.task.tracker.controller;

import com.task.tracker.authentication.service.AuthHelper;
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
    private final AuthHelper authHelper;

    @GetMapping("/month")
    public Heatmap getMonthlyHeatmap(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam int year,
            @RequestParam int month
    ) {
        return heatMapService.getOrCreateMonth(
                authHelper.extractUserId(authHeader),
                year,
                month
        );
    }

    @GetMapping("/year")
    public List<Heatmap> getYearHeatmap(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam int year
    ) {
        return heatMapService.getYearHeatmap(
                authHelper.extractUserId(authHeader),
                year
        );
    }

    @GetMapping("/day")
    public Heatmap getHeatmapForDay(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return heatMapService.getOrCreateMonth(
                authHelper.extractUserId(authHeader),
                date.getYear(),
                date.getMonthValue()
        );
    }
}

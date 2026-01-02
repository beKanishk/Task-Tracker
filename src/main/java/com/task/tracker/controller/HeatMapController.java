package com.task.tracker.controller;

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

    /**
     * Get heatmap for a specific month
     */
    @GetMapping("/{userId}/month")
    public Heatmap getMonthlyHeatmap(
            @PathVariable String userId,
            @RequestParam int year,
            @RequestParam int month
    ) {
        return heatMapService.getOrCreateMonth(userId, year, month);
    }

    /**
     * Get heatmap for whole year (list of months)
     */
    @GetMapping("/{userId}/year")
    public List<Heatmap> getYearHeatmap(
            @PathVariable String userId,
            @RequestParam int year
    ) {
        return heatMapService.getYearHeatmap(userId, year);
    }

    /**
     * Get heatmap for date of the day (used for UI quick fetch)
     */
    @GetMapping("/{userId}/day")
    public Heatmap getHeatmapForDay(
            @PathVariable String userId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return heatMapService.getOrCreateMonth(
                userId,
                date.getYear(),
                date.getMonthValue()
        );
    }
}

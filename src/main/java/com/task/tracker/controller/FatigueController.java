package com.task.tracker.controller;

import com.task.tracker.authentication.service.AuthHelper;
import com.task.tracker.model.UserFatigue;
import com.task.tracker.service.FatigueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/fatigue")
@RequiredArgsConstructor
public class FatigueController {

    private final FatigueService fatigueService;
    private final AuthHelper authHelper;

    @GetMapping
    public UserFatigue getFatigue(
            @RequestHeader("Authorization") String token
    ) {
        return fatigueService.getOrEvaluate(authHelper.extractUserId(token), LocalDate.now());
    }

    @PostMapping("/recompute")
    public UserFatigue recompute(
            @RequestHeader("Authorization") String token
    ) {
        return fatigueService.evaluateFatigue(authHelper.extractUserId(token), LocalDate.now());
    }
}

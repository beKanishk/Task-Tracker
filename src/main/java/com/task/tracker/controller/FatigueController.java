package com.task.tracker.controller;

import com.task.tracker.authentication.service.AuthService;
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
    private final AuthService authService;

    @GetMapping
    public UserFatigue getFatigue(
            @RequestHeader("Authorization") String token
    ) {
        String userId = authService.getUserFromToken(token).getId();
        return fatigueService.getOrEvaluate(userId, LocalDate.now());
    }

    /**
     * Force recompute
     */
    @PostMapping("/recompute")
    public UserFatigue recompute(
            @RequestHeader("Authorization") String token
    ) {
        String userId = authService.getUserFromToken(token).getId();
        return fatigueService.evaluateFatigue(userId, LocalDate.now());
    }
}

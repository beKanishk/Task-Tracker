package com.task.tracker.controller;

import com.task.tracker.authentication.service.AuthHelper;
import com.task.tracker.model.UserStreak;
import com.task.tracker.service.StreakService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/streak")
@RequiredArgsConstructor
public class StreakController {

    private final StreakService streakService;
    private final AuthHelper authHelper;

    @GetMapping
    public UserStreak getStreak(
            @RequestHeader("Authorization") String token
    ) {
        return streakService.getStreak(authHelper.extractUserId(token));
    }

    @PostMapping("/forgiveness/accept")
    public void acceptForgiveness(
            @RequestHeader("Authorization") String token
    ) {
        streakService.acceptForgiveness(authHelper.extractUserId(token), LocalDate.now());
    }

    @PostMapping("/forgiveness/decline")
    public void declineForgiveness(
            @RequestHeader("Authorization") String token
    ) {
        streakService.declineForgiveness(authHelper.extractUserId(token), LocalDate.now());
    }
}

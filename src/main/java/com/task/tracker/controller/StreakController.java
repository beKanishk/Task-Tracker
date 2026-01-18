package com.task.tracker.controller;

import com.task.tracker.authentication.service.AuthService;
import com.task.tracker.model.UserStreak;
import com.task.tracker.service.StreakService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/streak")
@RequiredArgsConstructor
public class StreakController {

    private final StreakService streakService;
    private final AuthService authService;

    @GetMapping
    public UserStreak getStreak(
            @RequestHeader("Authorization") String token
    ) {
        String userId = authService.getUserFromToken(token).getId();
        return streakService.getStreak(userId);
    }
}


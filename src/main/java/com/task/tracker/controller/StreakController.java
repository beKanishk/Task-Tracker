package com.task.tracker.controller;

import com.task.tracker.authentication.service.AuthService;
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
    private final AuthService authService;

    /* ================= READ STREAK ================= */

    @GetMapping
    public UserStreak getStreak(
            @RequestHeader("Authorization") String token
    ) {
        String userId = authService.getUserFromToken(token).getId();
        return streakService.getStreak(userId);
    }

    /* ================= ACCEPT FORGIVENESS ================= */

    @PostMapping("/forgiveness/accept")
    public void acceptForgiveness(
            @RequestHeader("Authorization") String token
    ) {
        String userId = authService.getUserFromToken(token).getId();
        streakService.acceptForgiveness(userId, LocalDate.now());
    }

    /* ================= DECLINE FORGIVENESS ================= */

    @PostMapping("/forgiveness/decline")
    public void declineForgiveness(
            @RequestHeader("Authorization") String token
    ) {
        String userId = authService.getUserFromToken(token).getId();
        streakService.declineForgiveness(userId, LocalDate.now());
    }
}

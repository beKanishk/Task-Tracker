package com.task.tracker.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AdminUserDTO {
    private String userId;
    private String name;
    private String username;
    private String email;
    private List<String> roles;
    private LocalDate createdAt;
    private LocalDateTime lastLogin;
    private long taskCount;
    private int currentStreak;
    private String fatigueLevel;
    private LocalDate lastActiveDate;
}

package com.task.tracker.controller;

import com.task.tracker.dto.AdminStatsDTO;
import com.task.tracker.dto.AdminUserDTO;
import com.task.tracker.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public AdminStatsDTO getStats() {
        return adminService.getStats();
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AdminUserDTO> getUsers() {
        return adminService.getUsers();
    }

    @PostMapping("/recompute/summaries")
    @PreAuthorize("hasRole('ADMIN')")
    public String recomputeAllSummaries() {
        int count = adminService.recomputeAllDailySummaries();
        return "Recomputed daily summaries for " + count + " users.";
    }

//    @PostMapping("/migrate/timezone")
//    @PreAuthorize("hasRole('ADMIN')")
//    public String migrateTimezone() {
//        return adminService.migrateTimezone();
//    }

//    @PostMapping("/migrate/users")
//    @PreAuthorize("hasRole('ADMIN')")
//    public String migrateUsers() {
//        return adminService.migrateUsers();
//    }
}

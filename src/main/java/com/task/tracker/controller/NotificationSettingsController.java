package com.task.tracker.controller;

import com.task.tracker.authentication.service.AuthHelper;
import com.task.tracker.dto.NotificationSettingsDTO;
import com.task.tracker.model.NotificationSettings;
import com.task.tracker.repository.NotificationSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationSettingsController {

    private final NotificationSettingsRepository notificationRepo;
    private final AuthHelper authHelper;

    private static final Pattern TIME_PATTERN = Pattern.compile("^([01]\\d|2[0-3]):[0-5]\\d$");

    @GetMapping("/settings")
    public NotificationSettings getSettings(@RequestHeader("Authorization") String token) {
        String userId = authHelper.extractUserId(token);
        return notificationRepo.findByUserId(userId)
                .orElseGet(() -> notificationRepo.save(
                        NotificationSettings.builder().userId(userId).build()
                ));
    }

    @PutMapping("/settings")
    public NotificationSettings updateSettings(
            @RequestHeader("Authorization") String token,
            @RequestBody NotificationSettingsDTO dto
    ) {
        String userId = authHelper.extractUserId(token);

        String time = dto.getReminderTime();
        if (time == null || !TIME_PATTERN.matcher(time).matches()) {
            throw new IllegalArgumentException("Invalid reminder time format. Use HH:mm (e.g. 09:00)");
        }

        NotificationSettings settings = notificationRepo.findByUserId(userId)
                .orElseGet(() -> NotificationSettings.builder().userId(userId).build());

        settings.setStreakWarningEnabled(dto.isStreakWarningEnabled());
        settings.setDailyReminderEnabled(dto.isDailyReminderEnabled());
        settings.setReminderTime(time);
        settings.setReminderTaskIds(dto.getReminderTaskIds() != null ? dto.getReminderTaskIds() : List.of());

        return notificationRepo.save(settings);
    }
}

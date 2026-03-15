package com.task.tracker.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class NotificationSettingsDTO {
    private boolean streakWarningEnabled;
    private boolean dailyReminderEnabled;
    private String reminderTime; // HH:mm format
    private List<String> reminderTaskIds = new ArrayList<>();
}

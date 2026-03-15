package com.task.tracker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "notification_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationSettings {

    @Id
    private String userId;

    @Builder.Default
    private boolean streakWarningEnabled = false;

    @Builder.Default
    private boolean dailyReminderEnabled = false;

    @Builder.Default
    private String reminderTime = "09:00"; // HH:mm in IST

    @Builder.Default
    private List<String> reminderTaskIds = new ArrayList<>(); // task IDs selected for daily reminder
}

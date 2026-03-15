package com.task.tracker.repository;

import com.task.tracker.model.NotificationSettings;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationSettingsRepository extends MongoRepository<NotificationSettings, String> {

    Optional<NotificationSettings> findByUserId(String userId);

    List<NotificationSettings> findByReminderTimeAndDailyReminderEnabledTrue(String reminderTime);

    List<NotificationSettings> findByReminderTimeAndStreakWarningEnabledTrue(String reminderTime);
}

package com.task.tracker.process;

import com.task.tracker.model.NotificationSettings;
import com.task.tracker.model.Task;
import com.task.tracker.model.UserStreak;
import com.task.tracker.repository.NotificationSettingsRepository;
import com.task.tracker.repository.TaskRepository;
import com.task.tracker.repository.UserRepository;
import com.task.tracker.repository.UserStreakRepository;
import com.task.tracker.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationCronJob {

    private final NotificationSettingsRepository notificationRepo;
    private final UserRepository userRepo;
    private final UserStreakRepository streakRepo;
    private final TaskRepository taskRepo;
    private final EmailService emailService;

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    @Scheduled(cron = "0 * * * * *") // runs every minute
    public void run() {
        String currentTime = LocalTime.now().format(TIME_FMT);
        LocalDate today = LocalDate.now();

        // Daily reminder — send with selected tasks
        List<NotificationSettings> reminders =
                notificationRepo.findByReminderTimeAndDailyReminderEnabledTrue(currentTime);
        for (NotificationSettings s : reminders) {
            List<Task> tasks = s.getReminderTaskIds() == null || s.getReminderTaskIds().isEmpty()
                    ? List.of()
                    : taskRepo.findAllById(s.getReminderTaskIds());
            userRepo.findById(s.getUserId())
                    .filter(u -> hasEmail(u.getEmail()))
                    .ifPresent(u -> emailService.sendDailyReminderEmail(u, tasks));
        }

        // Streak warning — only if user hasn't logged today
        List<NotificationSettings> warnings =
                notificationRepo.findByReminderTimeAndStreakWarningEnabledTrue(currentTime);
        for (NotificationSettings s : warnings) {
            UserStreak streak = streakRepo.findById(s.getUserId()).orElse(null);
            if (streak == null || streak.getLastActiveDate() == null) continue;
            if (streak.getLastActiveDate().equals(today)) continue; // already logged today

            userRepo.findById(s.getUserId())
                    .filter(u -> hasEmail(u.getEmail()))
                    .ifPresent(u -> emailService.sendStreakWarningEmail(u, streak.getCurrentStreak()));
        }

        if (!reminders.isEmpty() || !warnings.isEmpty()) {
            log.info("NotificationCronJob @ {}: {} reminders, {} streak warnings",
                    currentTime, reminders.size(), warnings.size());
        }
    }

    private boolean hasEmail(String email) {
        return email != null && !email.isBlank();
    }
}

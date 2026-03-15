package com.task.tracker.controller;

import com.task.tracker.model.Task;
import com.task.tracker.model.TaskType;
import com.task.tracker.model.User;
import com.task.tracker.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test/email")
@RequiredArgsConstructor
public class EmailTestController {

    private final EmailService emailService;

    @PostMapping("/streak-warning")
    public String testStreakWarning(@RequestParam String email,
                                    @RequestParam(defaultValue = "Test User") String name,
                                    @RequestParam(defaultValue = "7") int streak) {
        User user = User.builder().email(email).name(name).userName("test").build();
        emailService.sendStreakWarningEmail(user, streak);
        return "Streak warning email sent to " + email;
    }

    @PostMapping("/daily-reminder")
    public String testDailyReminder(@RequestParam String email,
                                     @RequestParam(defaultValue = "Test User") String name) {
        User user = User.builder().email(email).name(name).userName("test").build();
        List<Task> sampleTasks = List.of(
                Task.builder().title("Morning Run").taskType(TaskType.BOOLEAN).build(),
                Task.builder().title("Read 30 pages").taskType(TaskType.QUANTITATIVE).build()
        );
        emailService.sendDailyReminderEmail(user, sampleTasks);
        return "Daily reminder email sent to " + email;
    }
}

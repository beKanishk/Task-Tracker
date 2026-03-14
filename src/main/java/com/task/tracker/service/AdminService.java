package com.task.tracker.service;

import com.task.tracker.dto.AdminStatsDTO;
import com.task.tracker.dto.AdminUserDTO;
import com.task.tracker.model.Feedback;
import com.task.tracker.model.TaskProgress;
import com.task.tracker.model.UserFatigue;
import com.task.tracker.model.UserStreak;
import com.task.tracker.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final TaskProgressRepository taskProgressRepository;
    private final UserStreakRepository userStreakRepository;
    private final UserFatigueRepository userFatigueRepository;
    private final FeedbackRepository feedbackRepository;

    public AdminStatsDTO getStats() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);

        long activeToday = taskProgressRepository.findByDate(today)
                .stream().map(TaskProgress::getUserId).distinct().count();

        long activeThisWeek = taskProgressRepository.findByDateBetween(today.minusDays(6), today)
                .stream().map(TaskProgress::getUserId).distinct().count();

        Map<String, Long> feedbackByType = feedbackRepository.findAll()
                .stream().collect(Collectors.groupingBy(Feedback::getType, Collectors.counting()));

        return AdminStatsDTO.builder()
                .totalUsers(userRepository.count())
                .newUsersToday(userRepository.countByCreatedAt(today))
                .loggedInToday(userRepository.countByLastLoginBetween(startOfDay, endOfDay))
                .activeToday(activeToday)
                .activeThisWeek(activeThisWeek)
                .totalTasks(taskRepository.count())
                .tasksCreatedToday(taskRepository.countByCreatedAt(today))
                .feedbackByType(feedbackByType)
                .build();
    }

    public String migrateUsers() {
        List<com.task.tracker.model.User> users = userRepository.findAll();
        int count = 0;
        for (com.task.tracker.model.User user : users) {
            if (user.getCreatedAt() == null) {
                user.setCreatedAt(LocalDate.of(2026, 3, 14));
                userRepository.save(user);
                count++;
            }
        }
        return "Backfilled createdAt for " + count + " users.";
    }

    public List<AdminUserDTO> getUsers() {
        // Bulk-fetch everything in 4 queries total, then join in memory
        Map<String, Long> taskCountByUser = taskRepository.findAll()
                .stream().collect(Collectors.groupingBy(
                        t -> t.getUserId(), Collectors.counting()));

        Map<String, UserStreak> streakByUser = userStreakRepository.findAll()
                .stream().collect(Collectors.toMap(UserStreak::getUserId, Function.identity()));

        Map<String, UserFatigue> fatigueByUser = userFatigueRepository.findAll()
                .stream().collect(Collectors.toMap(UserFatigue::getUserId, Function.identity()));

        return userRepository.findAllByOrderByLastLoginDesc().stream().map(user -> {
            UserStreak streak = streakByUser.get(user.getId());
            UserFatigue fatigue = fatigueByUser.get(user.getId());

            return AdminUserDTO.builder()
                    .userId(user.getId())
                    .name(user.getName())
                    .username(user.getUserName())
                    .email(user.getEmail())
                    .roles(user.getRoles())
                    .createdAt(user.getCreatedAt())
                    .lastLogin(user.getLastLogin())
                    .taskCount(taskCountByUser.getOrDefault(user.getId(), 0L))
                    .currentStreak(streak != null ? streak.getCurrentStreak() : 0)
                    .fatigueLevel(fatigue != null ? fatigue.getLevel().name() : "N/A")
                    .lastActiveDate(streak != null ? streak.getLastActiveDate() : null)
                    .build();
        }).toList();
    }
}

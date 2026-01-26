package com.task.tracker.process;

import com.task.tracker.repository.UserRepository;
import com.task.tracker.service.FatigueService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class FatigueCronJob {

    private final UserRepository userRepository;
    private final FatigueService fatigueService;

    /**
     * Runs daily at 2 AM
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void evaluateDailyFatigue() {

        LocalDate today = LocalDate.now();

        userRepository.findAll()
                .forEach(user ->
                        fatigueService.evaluateFatigue(user.getId(), today)
                );
    }
}


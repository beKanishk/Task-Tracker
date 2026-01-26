package com.task.tracker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "user_fatigue")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFatigue {

    @Id
    private String userId;

    private int fatigueScore;          // 0–100
    private FatigueLevel level;         // NONE / LOW / MEDIUM / HIGH

    private LocalDate evaluatedOn;

    // diagnostics (for UI + GenAI later)
    private double completionTrend;     // negative = declining
    private int lowEffortDays;           // count in last N days
    private List<String> avoidedTasks;   // task titles
}

package com.task.tracker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Date;

@Document(collection = "daily_summary")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailySummary {

    @Id
    private String id;

    private String userId;

    private LocalDate date;

    private Integer totalProgressPercent;

    private Integer tasksCompleted;
    private Integer tasksInProgress;
}

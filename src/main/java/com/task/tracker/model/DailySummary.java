package com.task.tracker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "daily_summary")
@CompoundIndex(name = "user_date_idx", def = "{'userId':1,'date':1}", unique = true)
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

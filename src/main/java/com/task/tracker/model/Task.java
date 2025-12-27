package com.task.tracker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Date;

@Document(collection = "tasks")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Task {
    @Id
    private String id;

    private String userId;

    private String title;
    private String description;

    private TaskStatus status;

    private Integer targetHours;
    private Integer progressPercent;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate createdAt;
}

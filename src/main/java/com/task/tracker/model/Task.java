package com.task.tracker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "tasks")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Task {
    @Id
    private String id;

    @Indexed
    private String userId;

    private String title;
    private String description;

    private TaskStatus status;

    private TaskType taskType;

    private Integer targetValue;
    private String unit;
    private Integer progressPercent;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate createdAt;
}

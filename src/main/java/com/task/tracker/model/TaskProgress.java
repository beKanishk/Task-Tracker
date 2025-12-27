package com.task.tracker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Date;

@Document(collection = "task_progress")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskProgress {

    @Id
    private String id;

    private String taskId;
    private String userId;

    private LocalDate date;

    private Integer progressPercent;

    private Boolean completedToday;
}

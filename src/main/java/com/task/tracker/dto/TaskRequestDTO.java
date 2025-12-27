package com.task.tracker.dto;

import jakarta.annotation.Nullable;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskRequestDTO {

    private String userId;

    private String title;
    private String description;

    private LocalDate startDate;
    private LocalDate endDate;

    @Nullable
    private Integer targetHours;
}

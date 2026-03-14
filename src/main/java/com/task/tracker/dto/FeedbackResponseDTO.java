package com.task.tracker.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FeedbackResponseDTO {
    private String id;
    private String userId;
    private String message;
    private String type;
    private LocalDateTime createdAt;
}

package com.task.tracker.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserResponseDTO {
    private String id;
    private String userName;
    private String email;
    private String name;
    private Integer maxStreak;
}


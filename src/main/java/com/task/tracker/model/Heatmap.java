package com.task.tracker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("heatmap_month")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Heatmap {

    @Id
    private String id;

    private String userId;
    private int year;
    private int month;

    private List<Integer> activity;
}


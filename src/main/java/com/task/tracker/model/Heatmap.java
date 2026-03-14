package com.task.tracker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("heatmap_month")
@CompoundIndex(name = "user_year_month_idx", def = "{'userId':1,'year':1,'month':1}", unique = true)
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


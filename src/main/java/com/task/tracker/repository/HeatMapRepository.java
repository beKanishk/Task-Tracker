package com.task.tracker.repository;

import com.task.tracker.model.Heatmap;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


public interface HeatMapRepository extends MongoRepository<Heatmap, String> {
    Optional<Heatmap> findByUserIdAndYearAndMonth(String userId, int year, int month);

    List<Heatmap> findByUserIdAndYear(String userId, int year);
}

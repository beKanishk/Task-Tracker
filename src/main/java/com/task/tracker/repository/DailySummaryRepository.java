package com.task.tracker.repository;

import com.task.tracker.model.DailySummary;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

public interface DailySummaryRepository extends MongoRepository<DailySummary, String> {

    Optional<DailySummary> findByUserIdAndDate(String userId, LocalDate date);
}

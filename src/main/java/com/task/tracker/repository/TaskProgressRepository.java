package com.task.tracker.repository;

import com.task.tracker.model.TaskProgress;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TaskProgressRepository extends MongoRepository<TaskProgress, String> {

    // Projection: only fetch userId + date fields (faster for stats aggregation)
    @Query(value = "{ 'date': { $gte: ?0, $lte: ?1 } }", fields = "{ 'userId': 1, 'date': 1, '_id': 0 }")
    List<TaskProgress> findUserIdsByDateBetween(LocalDate start, LocalDate end);

    List<TaskProgress> findByTaskId(String taskId);

    void deleteByTaskId(String taskId);

    List<TaskProgress> findByUserIdAndDate(String userId, LocalDate date);

    TaskProgress findByUserIdAndDateAndTaskId(String userId, LocalDate date, String taskId);

    List<TaskProgress> findByUserIdAndDateBetween(String userId, LocalDate start, LocalDate end);

    List<TaskProgress> findByDate(LocalDate date);

    List<TaskProgress> findByDateBetween(LocalDate start, LocalDate end);
}

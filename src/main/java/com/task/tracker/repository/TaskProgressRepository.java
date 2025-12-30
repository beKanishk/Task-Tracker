package com.task.tracker.repository;

import com.task.tracker.model.TaskProgress;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public interface TaskProgressRepository extends MongoRepository<TaskProgress, String> {

    List<TaskProgress> findByTaskId(String taskId);

    List<TaskProgress> findByUserIdAndDate(String userId, LocalDate date);

    TaskProgress findByUserIdAndDateAndTaskId(String userId, LocalDate date, String taskId);

    List<TaskProgress> findByUserIdAndDateBetween(String userId, LocalDate start, LocalDate end);
}

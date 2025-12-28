package com.task.tracker.repository;

import com.task.tracker.model.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface TaskRepository extends MongoRepository<Task, String> {
    List<Task> findByUserId(String userId);

    @Query(value = "{}", fields = "{ 'userId' : 1 }")
    List<Task> findAllUserIdOnly();
}

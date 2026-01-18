package com.task.tracker.repository;

import com.task.tracker.model.UserStreak;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserStreakRepository extends MongoRepository<UserStreak, String> {
}

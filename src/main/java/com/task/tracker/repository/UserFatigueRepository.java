package com.task.tracker.repository;

import com.task.tracker.model.UserFatigue;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserFatigueRepository extends MongoRepository<UserFatigue, String> {
    Optional<UserFatigue> findByUserId(String userId);
}

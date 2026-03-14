package com.task.tracker.repository;

import com.task.tracker.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUserName(String userName);
    long countByCreatedAt(LocalDate date);
    long countByLastLoginBetween(LocalDateTime start, LocalDateTime end);
    List<User> findAllByOrderByLastLoginDesc();
}

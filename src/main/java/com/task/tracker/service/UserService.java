package com.task.tracker.service;

import com.task.tracker.dto.UserRequestDTO;
import com.task.tracker.dto.UserResponseDTO;
import com.task.tracker.model.User;
import com.task.tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserResponseDTO createUser(UserRequestDTO dto) {

        User user = User.builder()
                .userName(dto.getUserName())
                .password(dto.getPassword())  // hash it
                .email(dto.getEmail())
                .name(dto.getName())
                .maxStreak(0)
                .build();

        User saved = userRepository.save(user);

        return UserResponseDTO.builder()
                .id(saved.getId())
                .userName(saved.getUserName())
                .email(saved.getEmail())
                .name(saved.getName())
                .maxStreak(saved.getMaxStreak())
                .build();
    }
}

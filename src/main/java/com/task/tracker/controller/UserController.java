package com.task.tracker.controller;

import com.task.tracker.authentication.service.AuthService;
import com.task.tracker.dto.UserRequestDTO;
import com.task.tracker.dto.UserResponseDTO;
import com.task.tracker.model.User;
import com.task.tracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @PostMapping
    public UserResponseDTO createUser(@RequestBody UserRequestDTO dto) {
        return userService.createUser(dto);
    }
    @GetMapping("/me")
    public UserResponseDTO me(@RequestHeader("Authorization") String token) {
        return authService.getUserFromToken(token);
    }
}

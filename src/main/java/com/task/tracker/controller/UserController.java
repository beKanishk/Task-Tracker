package com.task.tracker.controller;

import com.task.tracker.authentication.service.AuthHelper;
import com.task.tracker.authentication.service.AuthService;
import com.task.tracker.dto.UserRequestDTO;
import com.task.tracker.dto.UserResponseDTO;
import com.task.tracker.model.User;
import com.task.tracker.repository.UserRepository;
import com.task.tracker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\S]+@[\\S]+\\.[\\S]+$");

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthHelper authHelper;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public UserResponseDTO createUser(@Valid @RequestBody UserRequestDTO dto) {
        return userService.createUser(dto);
    }

    @GetMapping("/me")
    public UserResponseDTO me(@RequestHeader("Authorization") String token) {
        return authService.getUserFromToken(token);
    }

    @PutMapping("/email")
    public ResponseEntity<?> updateEmail(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> body
    ) {
        String email = body.getOrDefault("email", "").trim();
        if (email.isEmpty() || !EMAIL_PATTERN.matcher(email).matches()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid email address"));
        }
        String userId = authHelper.extractUserId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEmail(email);
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("email", email));
    }
}

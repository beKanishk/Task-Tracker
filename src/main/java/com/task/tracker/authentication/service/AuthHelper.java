package com.task.tracker.authentication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthHelper {

    private final AuthService authService;

    public String extractUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            throw new IllegalArgumentException("Missing or invalid Authorization header");
        return authService.getUserFromToken(authHeader).getId();
    }
}

package com.task.tracker.controller;

import com.task.tracker.authentication.service.AuthHelper;
import com.task.tracker.dto.FeedbackRequestDTO;
import com.task.tracker.dto.FeedbackResponseDTO;
import com.task.tracker.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final AuthHelper authHelper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void submit(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody FeedbackRequestDTO dto
    ) {
        String userId = authHelper.extractUserId(authHeader);
        feedbackService.submit(userId, dto);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<FeedbackResponseDTO> getAll() {
        return feedbackService.getAll();
    }
}

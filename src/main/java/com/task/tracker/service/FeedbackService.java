package com.task.tracker.service;

import com.task.tracker.dto.FeedbackRequestDTO;
import com.task.tracker.dto.FeedbackResponseDTO;
import com.task.tracker.model.Feedback;
import com.task.tracker.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public void submit(String userId, FeedbackRequestDTO dto) {
        String type = (dto.getType() != null && !dto.getType().isBlank())
                ? dto.getType().toUpperCase()
                : "GENERAL";

        Feedback feedback = Feedback.builder()
                .userId(userId)
                .message(dto.getMessage().trim())
                .type(type)
                .createdAt(LocalDateTime.now())
                .build();

        feedbackRepository.save(feedback);
    }

    public List<FeedbackResponseDTO> getAll() {
        return feedbackRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(f -> FeedbackResponseDTO.builder()
                        .id(f.getId())
                        .userId(f.getUserId())
                        .message(f.getMessage())
                        .type(f.getType())
                        .createdAt(f.getCreatedAt())
                        .build())
                .toList();
    }
}

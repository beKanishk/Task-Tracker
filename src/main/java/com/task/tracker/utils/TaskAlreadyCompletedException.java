package com.task.tracker.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class TaskAlreadyCompletedException extends RuntimeException {
    public TaskAlreadyCompletedException(String message) {
        super(message);
    }
}

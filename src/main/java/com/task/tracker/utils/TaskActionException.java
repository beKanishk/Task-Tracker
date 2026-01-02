package com.task.tracker.utils;

public class TaskActionException extends RuntimeException{
    public  TaskActionException(String message) {
        super(message);
    }
    public TaskActionException(String message, Throwable cause) {}
}

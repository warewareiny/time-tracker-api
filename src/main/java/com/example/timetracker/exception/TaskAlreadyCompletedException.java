package com.example.timetracker.exception;

public class TaskAlreadyCompletedException extends RuntimeException {
    public TaskAlreadyCompletedException(Integer taskId) {
        super("Task with id " + taskId + " is already completed");
    }
}
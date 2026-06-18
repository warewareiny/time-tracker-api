package com.example.timetracker.task.exception;

public class TaskAlreadyCompletedException extends RuntimeException {
    public TaskAlreadyCompletedException(Integer taskId) {
        super("Task with id " + taskId + " is already completed");
    }
}